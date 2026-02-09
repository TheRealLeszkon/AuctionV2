package com.michael.AuctionV2.services;

import com.michael.AuctionV2.domain.dtos.TeamDTO;
import com.michael.AuctionV2.domain.dtos.requests.SubstituteRemovalRequest;
import com.michael.AuctionV2.domain.dtos.responses.*;
import com.michael.AuctionV2.domain.dtos.websocket.BidRequest;
import com.michael.AuctionV2.domain.dtos.websocket.WSEvent;
import com.michael.AuctionV2.domain.dtos.websocket.WebSocketEvent;
import com.michael.AuctionV2.domain.entities.*;
import com.michael.AuctionV2.domain.entities.enums.GameStatus;
import com.michael.AuctionV2.domain.entities.enums.IPLAssociation;
import com.michael.AuctionV2.domain.entities.enums.PlayerStatus;
import com.michael.AuctionV2.domain.entities.enums.TransactionType;
import com.michael.AuctionV2.domain.entities.keys.AuctionedPlayerId;
import com.michael.AuctionV2.domain.entities.keys.SetPlayerId;
import com.michael.AuctionV2.domain.mappers.*;
import com.michael.AuctionV2.repositories.AuctionedPlayerRepository;
import com.michael.AuctionV2.repositories.GameRepository;
import com.michael.AuctionV2.repositories.GameTransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    @PersistenceContext
    private EntityManager entityManager;
    private final GameRepository gameRepository;
    private final TeamService teamService;
    private final TeamMapper teamMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuctionedPlayerRepository auctionedPlayerRepository;
    private final GameTransactionRepository transactionRepository;
    private final  SetService setService;
    private final GameLogMapper gameLogMapper;
    private final PlayerService playerService;

    private final AllRounderStatsMapper allRounderStatsMapper;
    private final BatsmanStatsMapper batsmanStatsMapper;
    private final BowlerStatsMapper bowlerStatsMapper;
    public Game createGame(Game game){
        applyDefaults(game);
        return gameRepository.save(game);
    }

    private void applyDefaults(Game game) {
        game.setInitialBalance(
                Optional.ofNullable(game.getInitialBalance())
                        .orElse(GameDefaults.INITIAL_BALANCE)
        );
        game.setPlayersPerTeam(
                Optional.ofNullable(game.getPlayersPerTeam())
                        .orElse(GameDefaults.PLAYERS_PER_TEAM)
        );
        game.setBatsmenPerTeam(
                Optional.ofNullable(game.getBatsmenPerTeam())
                        .orElse(GameDefaults.BATSMEN_PER_TEAM)
        );
        game.setBowlersPerTeam(
                Optional.ofNullable(game.getBowlersPerTeam())
                        .orElse(GameDefaults.BOWLERS_PER_TEAM)
        );
        game.setAllRounderPerTeam(
                Optional.ofNullable(game.getAllRounderPerTeam())
                        .orElse(GameDefaults.ALL_ROUNDER_PER_TEAM)
        );
        game.setWicketKeeperPerTeam(
                Optional.ofNullable(game.getWicketKeeperPerTeam())
                        .orElse(GameDefaults.WICKET_KEEPER_PER_TEAM)
        );
        game.setUnCappedPerTeam(
                Optional.ofNullable(game.getUnCappedPerTeam())
                        .orElse(GameDefaults.UNCAPPED_PER_TEAM)
        );
        game.setLegendsPerTeam(
                Optional.ofNullable(game.getLegendsPerTeam())
                        .orElse(GameDefaults.LEGENDS_PER_TEAM)
        );
        game.setSpecialPlayersPerTeam(
                Optional.ofNullable(game.getSpecialPlayersPerTeam())
                        .orElse(GameDefaults.SPECIAL_PER_TEAM)
        );
        game.setForeignPlayersPerTeam(
                Optional.ofNullable(game.getForeignPlayersPerTeam())
                        .orElse(GameDefaults.FOREIGN_PER_TEAM)
        );
        game.setSubstitutesPerTeam(
                Optional.ofNullable(game.getSubstitutesPerTeam())
                        .orElse(GameDefaults.SUBSTITUTES_PER_TEAM)
        );
    }


    public boolean existsByName(String name){
        return gameRepository.existsByName(name);
    }
    public boolean existsById(Integer gameId){
        return gameRepository.existsById(gameId);
    }
    public Game findById(Integer id){
        return gameRepository.findById(id).orElseThrow(() ->new IllegalArgumentException("No Game by Id: "+id+" Exists!"));
    }
    @Transactional
    public Game InitializeGame(Integer gameId){
        Game game = findById(gameId);
        if(game.getStatus()== GameStatus.ACTIVE){
            throw new IllegalStateException("Game ID: "+gameId+" Game Name: "+game.getName()+ " is already active! Can't Initialize Active game!");
        }
        if(game.getStatus()!=GameStatus.INACTIVE){
            throw new IllegalStateException("Game ID: "+gameId+ " Game Name: "+game.getName()+ " has been "+game.getStatus()+ " It Can't be restarted!");
        }
        game.setStatus(GameStatus.ACTIVE);

        //Create 10 Teams for the game
        teamService.createTeamsForGame(game);

        //Initalize the player list
        List<AuctionedPlayer> playerToBeRegistered =setService.findAllPlayersOfSet(game.getSetId()).stream().map(setPlayer ->
                    AuctionedPlayer.builder()
                            .auctionedPlayerId(new AuctionedPlayerId(gameId,setPlayer.getId().getPlayerId()))
                            .playerStatus(PlayerStatus.UNSOLD)
                            .build()
        ).toList();
        auctionedPlayerRepository.saveAll(playerToBeRegistered);
        return game;
    }

    public AuctionedPlayer addAuctionedPlayer(AuctionedPlayer auctionedPlayer){
        return auctionedPlayerRepository.save(auctionedPlayer);
    }
    @Transactional
    public PurchasedPlayer purchasePlayerForTeam(Integer gameId, Integer playerId, IPLAssociation teamAssociation, BigDecimal bidAmount){
        //General Checks
        Game game =findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        if(bidAmount.signum()<=0){
            throw new IllegalArgumentException("The bid amount must not be negative or zero!");
        }

        //Check if player already sold
        AuctionedPlayerId auctionedPlayerId =new AuctionedPlayerId(gameId,playerId);
        AuctionedPlayer foundPlayer =auctionedPlayerRepository.findById(auctionedPlayerId)
                .orElseThrow(() -> new IllegalStateException("Player not registered for auction!"));
        if (foundPlayer.getPlayerStatus() == PlayerStatus.SOLD) {
            throw new IllegalStateException("Player already sold");
        }

        //Check if team has enough balance
        SetPlayer playerDetails =setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),playerId));
        if(bidAmount.compareTo( playerDetails.getPrice())<0){
            throw new IllegalStateException("Can't sell player below base price!");
        }

        Player playerBioDetails = playerService.findPlayerById(playerDetails.getId().getPlayerId());
        Team team = teamService.getTeamOfAssociationInGame(gameId,teamAssociation);

        // final updates
        teamService.checkConstraintsAndUpdateTeamCounts(
                team,
                playerBioDetails.getType(),
                playerBioDetails.getIsUncapped(),
                playerBioDetails.getIsLegend(),
                playerBioDetails.getIsForeign(),
                game
        );
        teamService.updateTeamForPurchase(team,bidAmount,playerDetails.getPoints());

        foundPlayer.setTeamId(team.getId());
        foundPlayer.setSoldPrice(bidAmount);
        foundPlayer.setPlayerStatus(PlayerStatus.SOLD);

        PurchasedPlayer purchasedPlayer =PurchasedPlayer.builder()
                .playerId(playerId)
                .name(playerBioDetails.getName())
                .playerType(playerBioDetails.getType())
                .boughtFor(bidAmount)
                .points(playerDetails.getPoints())
                .isForeign(playerBioDetails.getIsForeign())
                .isLegend(playerBioDetails.getIsLegend())
                .isUncapped(playerBioDetails.getIsUncapped())
                .build();

        GameTransaction transaction=transactionRepository.save(
                GameTransaction.builder()
                        .game(game)
                        .player(playerBioDetails)
                        .team(team)
                        .amount(bidAmount)
                        .playerStatus(PlayerStatus.SOLD)
                        .type(TransactionType.PURCHASE)
                        .build()
        );

        String teamPurchaseUpdatesDestination ="/topic/game/"+gameId+"/purchases/"+teamAssociation;
        messagingTemplate.convertAndSend(
                teamPurchaseUpdatesDestination,
                new WebSocketEvent<PurchasedPlayer>(WSEvent.PURCHASE, Instant.now(),purchasedPlayer)
        );
        String teamUpdatesDestination ="/topic/game/"+gameId+"/team/"+teamAssociation;
        messagingTemplate.convertAndSend(
                teamUpdatesDestination,
                new WebSocketEvent<TeamDTO>(WSEvent.TEAM_UPDATE,Instant.now(),teamMapper.toDTO(team))
        );
        String gameAuditDestination = "/topic/game/"+gameId+"/audit";
        messagingTemplate.convertAndSend(
                gameAuditDestination,
                new WebSocketEvent<GameLog>(WSEvent.AUDIT,Instant.now(),gameLogMapper.toDTO(transaction))
        );
        return purchasedPlayer;
    }

    public List<PurchasedPlayer> getTeamPurchases(Team team,Game game){
        Integer setId =game.getSetId();
        List<AuctionedPlayer> auctionPlayers = auctionedPlayerRepository.findByTeamId(team.getId());
        List<PurchasedPlayer> teamPurchases = auctionPlayers.stream()
                .map(purchaseEntry ->{
                    Integer playerId =purchaseEntry.getAuctionedPlayerId().getPlayerId();
                    Player playerBioData = playerService.findPlayerById(playerId);
                    SetPlayer playerGameData = setService.findPlayerDetailsInSetById(new SetPlayerId(setId,playerId));
                    log.info("{} was fetched!",playerBioData.getName());
                    log.info("{} <- legend status",playerBioData.getIsLegend());
                    return PurchasedPlayer.builder()
                            .playerId(playerId)
                            .playerType(playerBioData.getType())
                            .name(playerBioData.getName())
                            .boughtFor(purchaseEntry.getSoldPrice())
                            .points(playerGameData.getPoints())
                            .isLegend(playerBioData.getIsLegend())
                            .isForeign(playerBioData.getIsForeign())
                            .isUncapped(playerBioData.getIsUncapped())
                            .build();
                }).toList();
        return teamPurchases;
    }

    @Transactional
    public RefundConfirmation refundPlayer(Integer gameId,Integer playerId){
        Game game = findById(gameId);
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        AuctionedPlayer purchaseRecord= auctionedPlayerRepository.findById(new AuctionedPlayerId(game.getId(),playerId))
                .orElseThrow(() ->new IllegalStateException("No such player registered in auction!"));
        if(purchaseRecord.getPlayerStatus()!=PlayerStatus.SOLD){
            throw new IllegalStateException("Not sold players can't be refunded!");
        }
        if (purchaseRecord.getTeamId() == null) {
            throw new IllegalStateException("Refund failed: player is not owned by any team");
        }

        SetPlayer playerGameDetails = setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(), playerId));
        Player playerBioDetails = playerService.findPlayerById(playerId);
        Team team =teamService.findTeamById(purchaseRecord.getTeamId());
        team.setBalance(team.getBalance().add(purchaseRecord.getSoldPrice()));
        team.setPoints(team.getPoints() -playerGameDetails.getPoints());
        teamService.reduceTeamPlayerCounts(team,playerBioDetails);
        BigDecimal refundedAmount = purchaseRecord.getSoldPrice();
        purchaseRecord.setTeamId(null);
        purchaseRecord.setSoldPrice(null);
        purchaseRecord.setPlayerStatus(PlayerStatus.UNSOLD);
        RefundConfirmation confirmation =new RefundConfirmation(
                playerBioDetails.getName(),
                purchaseRecord.getPlayerStatus(),
                "Refund was successful!"
        );

        GameTransaction transaction =transactionRepository.save(
                GameTransaction.builder()
                        .game(game)
                        .player(playerBioDetails)
                        .team(team)
                        .type(TransactionType.REFUND)
                        .playerStatus(PlayerStatus.UNSOLD)
                        .amount(refundedAmount)
                .build()
        );
        String teamRefundUpdatesDestination = "/topic/game/"+gameId+"/refunds/"+team.getAssociation(); // send to "/topic/test" for testing
        messagingTemplate.convertAndSend(
                teamRefundUpdatesDestination,
                new WebSocketEvent<RefundConfirmation>(WSEvent.REFUND,Instant.now(),confirmation)
        );
        String teamUpdatesDestination ="/topic/game/"+gameId+"/team/"+team.getAssociation();
        messagingTemplate.convertAndSend(
                teamUpdatesDestination,
                new WebSocketEvent<TeamDTO>(WSEvent.TEAM_UPDATE,Instant.now(),teamMapper.toDTO(team))
        );
        String gameAuditDestination = "/topic/game/"+gameId+"/audit";
        messagingTemplate.convertAndSend(
                gameAuditDestination,
                new WebSocketEvent<GameLog>(WSEvent.AUDIT,Instant.now(),gameLogMapper.toDTO(transaction))
        );
        return confirmation;
    }

    public PlayerStatus getPlayerStatusInGame(Integer playerId, Game game) {
        AuctionedPlayer auctionRecord =findAuctionedPlayerById(new AuctionedPlayerId(game.getId(),playerId));
        return auctionRecord.getPlayerStatus();
    }
    public AuctionedPlayer findAuctionedPlayerById(AuctionedPlayerId auctionedPlayerId){
        return  auctionedPlayerRepository.findById(auctionedPlayerId)
                .orElseThrow(() -> new IllegalStateException("Player not registered in game!"));
    }

    public List<GameLog> getAllGameLogs(Integer gameId){
        Game game = findById(gameId);
        if(game.getStatus()==GameStatus.INACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        List<GameTransaction> allTransactions = transactionRepository.findAllGameTransactionByGameIdOrderByIdDesc(gameId);
        return allTransactions.stream().map(gameLogMapper::toDTO).toList();
    }

    public void broadcastCurrentBid(BidRequest request,Integer gameId){
        String bidingDestination = "/topic/game/"+gameId+"/bids";
        try{
            Game game = findById(gameId);
            if(game.getStatus() != GameStatus.ACTIVE){
                WebSocketEvent<String> errorMessage = new WebSocketEvent<String>(
                        WSEvent.ERROR,
                        Instant.now(),
                        "Game wtih ID:"+gameId+ " is not active! "
                );
                messagingTemplate.convertAndSend(
                        bidingDestination,
                        errorMessage
                );
                log.info("[Websocket]: Subscription {} => Game wtih ID: {} is not active! ",bidingDestination,gameId);
                return;
            }
        }catch (IllegalArgumentException ex){
            WebSocketEvent<String> errorMessage = new WebSocketEvent<String>(
                    WSEvent.ERROR,
                    Instant.now(),
                    "Can't get or send bids for non-existent game! Given game ID: "+gameId
            );
            messagingTemplate.convertAndSend(
                    bidingDestination,
                    errorMessage
            );
            log.info("[Websocket]: Subscription {} => Can't get or send bids for non-existent game! Given game ID: {} ",bidingDestination,gameId);
            return;
        }
        WebSocketEvent<BidRequest> event = new WebSocketEvent<>(
                WSEvent.BID,
                Instant.now(),
                request
        );
        messagingTemplate.convertAndSend(
                bidingDestination,
                event
        );
        log.info("[Websocket]: Subscription {} => successful bid broadcast for game ID: {}",bidingDestination,gameId );
    }

    public List<Game> getAllGames(){
        return gameRepository.findAll();
    }

    public List<CompletePlayer> getAllPlayersBySoldStatus(Integer gameId,PlayerStatus status) {
        Game game = findById(gameId);
        if(game.getStatus()==GameStatus.INACTIVE){
            throw new IllegalArgumentException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        List<AuctionedPlayer> auctionRecords = auctionedPlayerRepository.findAllByAuctionedPlayerId_GameIdAndPlayerStatusOrderByAuctionedPlayerId_PlayerId(gameId,status);
        return auctionRecords.stream().map(
                record ->{
                    Player playerBioData = playerService.findPlayerById(record.getAuctionedPlayerId().getPlayerId());
                    SetPlayer playerDetails = setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),record.getAuctionedPlayerId().getPlayerId()));
                    return CompletePlayer.builder()
                            .id(playerBioData.getId())
                            .name(playerBioData.getName())
                            .imageLink(playerBioData.getImageLink())
                            .type(playerBioData.getType())
                            .isLegend(playerBioData.getIsLegend())
                            .isUncapped(playerBioData.getIsUncapped())
                            .isForeign(playerBioData.getIsForeign())
                            .country(playerBioData.getCountry())
                            .batsmanStats(batsmanStatsMapper.toDTO(playerBioData.getBatsmenStats()))
                            .bowlerStats(bowlerStatsMapper.toDTO(playerBioData.getBowlerStats()))
                            .allRounderStats(allRounderStatsMapper.toDTO(playerBioData.getAllRounderStats()))
                            .setId(playerDetails.getId().getSetId())
                            .price(playerDetails.getPrice())
                            .points(playerDetails.getPoints())
                            .order(playerDetails.getOrder())
                            .status(record.getPlayerStatus())
                            .build();
                }
        ).toList();
    }

    public List<Ranking> getRankings(Integer gameId) {
        Game game = findById(gameId);
        if(game.getStatus()!=GameStatus.ENDED){
            throw new IllegalStateException("Can't publish results of game before it has ended!");
        }
        List<Team> teams =
                teamService.getAllTeamsOfGame(gameId)
                        .stream()
                        .sorted(
                                Comparator.comparing(Team::isQualified).reversed()
                                        .thenComparing(Comparator.comparingInt(Team::getPoints).reversed())
                                        .thenComparing(Team::getBalance, Comparator.reverseOrder())
                        )
                        .toList();

        int rank = 1;
        List<Ranking> rankings = new ArrayList<>();
        for(Team team:teams){
            Ranking ranking = new Ranking();
            TeamDTO teamDTO =teamMapper.toDTO(team);
            teamDTO.setQualified(team.isQualified());
            List<String> finalTeam =auctionedPlayerRepository.findByTeamId(team.getId()).stream().filter(purchaseRecord -> purchaseRecord.getPlayerStatus() == PlayerStatus.SOLD)
                    .map(purchaseRecord ->{
                        Player player = playerService.findPlayerById(purchaseRecord.getAuctionedPlayerId().getPlayerId());
                        return player.getName();
                    }).toList();
            List<String> substitutes =auctionedPlayerRepository.findByTeamId(team.getId()).stream().filter(purchaseRecord -> purchaseRecord.getPlayerStatus() == PlayerStatus.SUBSTITUTED)
                    .map(purchaseRecord ->{
                        Player player = playerService.findPlayerById(purchaseRecord.getAuctionedPlayerId().getPlayerId());
                        return player.getName();
                    }).toList();
            ranking.setPlace(rank++);
            ranking.setTeamStats(teamDTO);
            ranking.setIsQualified(team.isQualified());
            ranking.setFinalTeam(finalTeam);
            ranking.setSubstitutes(substitutes);
            rankings.add(ranking);
        }
        return rankings;
    }
    @Transactional
    public List<Ranking> endGame(Integer gameId){
        Game game = findById(gameId);
        disqualifyTeams(game);
        entityManager.flush();

        if(game.getStatus()!=GameStatus.FINALIZED){
            throw new IllegalStateException("Can't End a game that isn't Finalized!");
        }
        game.setStatus(GameStatus.ENDED);
        List<Ranking> rankings =getRankings(gameId);
        return rankings;
    }

    public void disqualifyTeams(Game game) {
        List<Team> teams = teamService.getAllTeamsOfGame(game.getId());
        for(Team team: teams){
            team.setQualified(isTeamQualified(team,game));
        }
    }

    @Transactional
    public void finalizeGame(Integer gameId){
        Game game = findById(gameId);
        if (game.getStatus() == GameStatus.ENDED) {
            throw new IllegalStateException("Game of ID: "+gameId+" has already ENDED!");
        }
        if(game.getStatus()!=GameStatus.ACTIVE){
            throw new IllegalStateException("Game of ID: "+gameId+" is not ACTIVE!");
        }
        game.setStatus(GameStatus.FINALIZED);
    }
    @Transactional
    public void unfinalizeGame(Integer gameId){
        Game game = findById(gameId);
        if (game.getStatus() != GameStatus.FINALIZED) {
            throw new IllegalStateException("Game of ID: " + gameId + " is not FINALIZED and cannot be resumed!");
        }
        game.setStatus(GameStatus.ACTIVE);
    }
    private boolean isTeamQualified(Team team, Game game) {
        if(team.getPlayerCount() > game.getPlayersPerTeam()) return false;
        if (!Objects.equals(team.getPlayerCount(), game.getPlayersPerTeam())) return false;
        if (team.getForeignCount() < game.getForeignPlayersPerTeam()) return false;
        if (team.getSpecialCount() < game.getSpecialPlayersPerTeam()) return false;
        if (team.getBatsmanCount() < game.getBatsmenPerTeam()) return false;
        if (team.getBowlerCount() < game.getBowlersPerTeam()) return false;
        if (team.getAllRounderCount() < game.getAllRounderPerTeam()) return false;
        if (team.getWicketKeeperCount() < game.getWicketKeeperPerTeam()) return false;

        return true;
    }
    @Transactional
    public void removeSubstitutes(Integer gameId, SubstituteRemovalRequest removalRequest) {
        Game game = findById(gameId);
        Team team = teamService.getTeamOfAssociationInGame(gameId, removalRequest.getTeamAssociation());

        // 1. Basic State Validations
        if (game.getStatus() != GameStatus.FINALIZED) {
            throw new IllegalStateException("Game is not FINALIZED! Auction must end first.");
        }
        if (team.isSelectionLocked()) {
            throw new IllegalStateException("Team selection is already locked!");
        }

        List<Integer> requestedSubIds = removalRequest.getSubstitutes();
        int currentCount = team.getPlayerCount();
        int targetCount = game.getPlayersPerTeam();

        // 2. Optimized Validation Logic
        // Case A: Team is already at or below the limit and sent no subs
        if (requestedSubIds.isEmpty() && currentCount <= targetCount) {
            lockTeamSelection(team, game);
            return;
        }

        // Case B: Validation of removal count
        int countAfterRemoval = currentCount - requestedSubIds.size();
        if (countAfterRemoval < targetCount) {
            throw new IllegalArgumentException("Cannot remove players below the required count of " + targetCount);
        }

        // Ensure they are removing enough to meet the game's sub-quota rules
        if (requestedSubIds.size() < game.getSubstitutesPerTeam() && countAfterRemoval > targetCount) {
            throw new IllegalStateException("You must remove more substitutes to reach the roster limit.");
        }

        // 3. Bulk Fetch Records (Optimization)
        List<AuctionedPlayerId> playerIds = requestedSubIds.stream()
                .map(id -> new AuctionedPlayerId(gameId, id))
                .toList();

        List<AuctionedPlayer> purchaseRecords = auctionedPlayerRepository.findAllByAuctionedPlayerIdInAndTeamId(playerIds, team.getId());

        // 4. Security Check
        if (purchaseRecords.size() != requestedSubIds.size()) {
            throw new IllegalStateException("One or more players selected do not belong to your team.");
        }

        // 5. Bulk Fetch Player Details (Performance: Reduces N+1)
        // Assuming setService has a bulk lookup method; if not, consider adding one.
        for (AuctionedPlayer record : purchaseRecords) {
            processPlayerRemoval(team, record, game.getSetId());
        }

        // 6. Finalize
        lockTeamSelection(team, game);
    }

    private void processPlayerRemoval(Team team, AuctionedPlayer record, Integer setId) {
        SetPlayer playerDetails = setService.findPlayerDetailsInSetById(
                new SetPlayerId(setId, record.getAuctionedPlayerId().getPlayerId())
        );
        Player player = playerService.findPlayerById(playerDetails.getId().getPlayerId());

        team.setPoints(team.getPoints() - playerDetails.getPoints());
        teamService.reduceTeamPlayerCounts(team, player);
        record.setPlayerStatus(PlayerStatus.SUBSTITUTED);
    }

    private void lockTeamSelection(Team team, Game game) {
        team.setQualified(isTeamQualified(team, game));
        team.setSelectionLocked(true);
        log.info("Game #{} | Team {} has locked their selection.", game.getId(), team.getAssociation());
    }
    public void publishResults(Integer gameId) {
        Game game = findById(gameId);
        if(game.getStatus()!= GameStatus.ENDED) throw new IllegalStateException("Cant Publish The Result of a game that hasn't ended!");
        List<Team> teams = teamService.getAllTeamsOfGame(gameId);
        List<Ranking> rankings = getRankings(gameId);
        for (Team team: teams){
            String resultDestination= "/game/"+gameId+"/results/"+team.getAssociation();
            Ranking ranking = rankings.stream().filter(rank -> rank.getTeamStats().getAssociation() == team.getAssociation()).toList().get(0);
            messagingTemplate.convertAndSend(
                    resultDestination,
                    new WebSocketEvent<Ranking>(
                            WSEvent.RESULT,
                            Instant.now(),
                            ranking
                    )
            );
        }

    }

    public CompletePlayer findAuctionPlayerByName(String name,Integer gameId) {
        Game game = findById(gameId);
        Player playerBioData =playerService.findPlayerByName(name);
        SetPlayer playerDetails = setService.findPlayerDetailsInSetById(new SetPlayerId(game.getSetId(),playerBioData.getId()));
        return CompletePlayer.builder()
                .id(playerBioData.getId())
                .name(playerBioData.getName())
                .imageLink(playerBioData.getImageLink())
                .type(playerBioData.getType())
                .isLegend(playerBioData.getIsLegend())
                .isUncapped(playerBioData.getIsUncapped())
                .isForeign(playerBioData.getIsForeign())
                .country(playerBioData.getCountry())
                .batsmanStats(batsmanStatsMapper.toDTO(playerBioData.getBatsmenStats()))
                .bowlerStats(bowlerStatsMapper.toDTO(playerBioData.getBowlerStats()))
                .allRounderStats(allRounderStatsMapper.toDTO(playerBioData.getAllRounderStats()))
                .setId(game.getSetId())
                .price(playerDetails.getPrice())
                .points(playerDetails.getPoints())
                .order(playerDetails.getOrder())
                .build();
    }

    public LockedInUpdate getLockedInStatus(Integer gameId) {
        Game game = findById(gameId);
        if(game.getStatus()== GameStatus.ACTIVE || game.getStatus() == GameStatus.INACTIVE){
            throw new IllegalStateException("Game is not FINALIZED OR ENDED. Can't perform operation now.");
        }
        return new LockedInUpdate(
                teamService.getNumberOfSelectionLockedTeams(gameId),
                teamService.getAllLockedInTeamsOfGame(gameId).stream().map(team -> team.getAssociation().toString()).toList()
        );
    }
}
