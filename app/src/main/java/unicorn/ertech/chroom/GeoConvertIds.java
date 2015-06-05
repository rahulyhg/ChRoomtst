package unicorn.ertech.chroom;

/**
 * Created by UNICORN on 06.04.2015.
 */
public class GeoConvertIds {
    static int region;

    public static int getServerRegionId(int appRegionId){
        switch(appRegionId){
            case 0:
                region=0;
                return 4312;
            case 1:
                region=1;
                return 4925;
            case 2:
                region=2;
                return 1998532;
            case 3:
                region=3;
                return 3160;
            case 4:
                region=4;
                return 3223;
            case 5:
                region=5;
                return 3251;
            case 6:
                region=6;
                return 3282;
            case 7:
                region=7;
                return 3296;
            case 8:
                region=8;
                return 3352;
            case 9:
                region=9;
                return 3371;
            case 10:
                region=10;
                return 3407;
            case 11:
                region=11;
                return 3437;
            case 12:
                region=12;
                return 3468;
            case 13:
                region=13;
                return 3503;
            case 14:
                region=14;
                return 3529;
            case 15:
                region=15;
                return 3630;
            case 16:
                region=16;
                return 3673;
            case 17:
                region=17;
                return 3675;
            case 18:
                region=18;
                return 3703;
            case 19:
                region=19;
                return 3751;
            case 20:
                region=20;
                return 3761;
            case 21:
                region=21;
                return 3827;
            case 22:
                region=22;
                return 3841;
            case 23:
                region=23;
                return 3872;
            case 24:
                region=24;
                return 3892;
            case 25:
                region=25;
                return 3291;
            case 26:
                region=26;
                return 3952;
            case 27:
                region=27;
                return 3994;
            case 28:
                region=28;
                return 4026;
            case 29:
                region=29;
                return 4052;
            case 30:
                region=30;
                return 4105;
            case 31:
                region=31;
                return 4176;
            case 32:
                region=32;
                return 4198;
            case 33:
                region=33;
                return 4227;
            case 34:
                region=34;
                return 4243;
            case 35:
                region=35;
                return 4270;
            case 36:
                region=36;
                return 4287;
            case 37:
                region=37;
                return 4481;
            case 38:
                region=38;
                return 3563;
            case 39:
                region=39;
                return 4503;
            case 40:
                region=40;
                return 4528;
            case 41:
                region=41;
                return 4561;
            case 42:
                region=42;
                return 4593;
            case 43:
                region=43;
                return 4633;
            case 44:
                region=44;
                return 4657;
            case 45:
                region=45;
                return 4689;
            case 46:
                region=46;
                return 4734;
            case 47:
                region=47;
                return 4773;
            case 48:
                region=48;
                return 4800;
            case 49:
                region=49;
                return 4861;
            case 50:
                region=50;
                return 4891;
            case 51:
                region=51;
                return 4969;
            case 52:
                region=52;
                return 5011;
            case 53:
                region=53;
                return 5052;
            case 54:
                region=54;
                return 5080;
            case 55:
                region=55;
                return 5151;
            case 56:
                region=56;
                return 5161;
            case 57:
                region=57;
                return 5191;
            case 58:
                region=58;
                return 5225;
            case 59:
                region=59;
                return 5246;
            case 60:
                region=60;
                return 3784;
            case 61:
                region=61;
                return 5291;
            case 62:
                region=62;
                return 5312;
            case 63:
                region=63;
                return 5326;
            case 64:
                region=64;
                return 5356;
            case 65:
                region=65;
                return 5404;
            case 66:
                region=66;
                return 5432;
            case 67:
                region=67;
                return 5458;
            case 68:
                region=68;
                return 5473;
            case 69:
                region=69;
                return 2316497;
            case 70:
                region=70;
                return 5507;
            case 71:
                region=71;
                return 5543;
            case 72:
                region=72;
                return 5555;
            case 73:
                region=73;
                return 5600;
            case 74:
                region=74;
                return 2415585;
            case 75:
                region=75;
                return 5019394;
            case 76:
                region=76;
                return 5625;
            default:
                return 4312;
        }
    }

    public static int getAppRegionId(int serverRegionId){
        switch(serverRegionId){
            case 4312:
                region = 0;
                return 0;
            case 4925:
                region=1;
                return 1;
            case 1998532:
                region=2;
                return 2;
            case 3160:
                region=3;
                return 3;
            case 3223:
                region=4;
                return 4;
            case 3251:
                region=5;
                return 5;
            case 3282:
                region=6;
                return 6;
            case 3296:
                region=7;
                return 7;
            case 3352:
                region=8;
                return 8;
            case 3371:
                region=9;
                return 9;
            case 3407:
                region=10;
                return 10;
            case 3437:
                region=11;
                return 11;
            case 3468:
                region=12;
                return 12;
            case 3503:
                region=13;
                return 13;
            case 3529:
                region=14;
                return 14;
            case 3630:
                region=15;
                return 15;
            case 3673:
                region=16;
                return 16;
            case 3675:
                region=17;
                return 17;
            case 3703:
                region=18;
                return 18;
            case 3751:
                region=19;
                return 19;
            case 3761:
                region=20;
                return 20;
            case 3827:
                region=21;
                return 21;
            case 3841:
                region=22;
                return 22;
            case 3872:
                region=23;
                return 23;
            case 3892:
                region=24;
                return 24;
            case 3291:
                region=25;
                return 25;
            case 3952:
                region=26;
                return 26;
            case 3994:
                region=27;
                return 27;
            case 4026:
                region=28;
                return 28;
            case 4052:
                region=29;
                return 29;
            case 4105:
                region=30;
                return 30;
            case 4176:
                region=31;
                return 31;
            case 4198:
                region=32;
                return 32;
            case 4227:
                region=33;
                return 33;
            case 4243:
                region=34;
                return 34;
            case 4270:
                region=35;
                return 35;
            case 4287:
                region=36;
                return 36;
            case 4481:
                region=37;
                return 37;
            case 3563:
                region=38;
                return 38;
            case 4503:
                region=39;
                return 39;
            case 4528:
                region=40;
                return 40;
            case 4561:
                region=41;
                return 41;
            case 4593:
                region=42;
                return 42;
            case 4633:
                region=43;
                return 43;
            case 4657:
                region=44;
                return 44;
            case 4689:
                region=45;
                return 45;
            case 4734:
                region=46;
                return 46;
            case 4773:
                region=47;
                return 47;
            case 4800:
                region=48;
                return 48;
            case 4861:
                region=49;
                return 49;
            case 4891:
                region=50;
                return 50;
            case 4969:
                region=51;
                return 51;
            case 5011:
                region=52;
                return 52;
            case 5052:
                region=53;
                return 53;
            case 5080:
                region=54;
                return 54;
            case 5151:
                region=55;
                return 55;
            case 5161:
                region=56;
                return 56;
            case 5191:
                region=57;
                return 57;
            case 5225:
                region=58;
                return 58;
            case 5246:
                region=59;
                return 59;
            case 3784:
                region=60;
                return 60;
            case 5291:
                region=61;
                return 61;
            case 5312:
                region=62;
                return 62;
            case 5326:
                region=63;
                return 63;
            case 5356:
                region=64;
                return 64;
            case 5404:
                region=65;
                return 65;
            case 5432:
                region=66;
                return 66;
            case 5458:
                region=67;
                return 67;
            case 5473:
                region=68;
                return 68;
            case 2316497:
                region=69;
                return 69;
            case 5507:
                region=70;
                return 70;
            case 5543:
                region=71;
                return 71;
            case 5555:
                region=72;
                return 72;
            case 5600:
                region=73;
                return 73;
            case 2415585:
                region=74;
                return 74;
            case 5019394:
                region=75;
                return 75;
            case 5625:
                region=76;
                return 76;
            default:
                region=0;
                return 0;
        }
    }

    public static int getServerCityId(int appCityId){
        switch(region){
            case 0:
                return getServerCitiesMsk(appCityId);
            case 1:
                return getServerCitiesSpb(appCityId);
            case 2:
                return getServerCityAdygea(appCityId);
            case 3:
                return getServerCityAltayskiykra(appCityId);
            case 4:
                return getServerCityAmyrskayoblast(appCityId);
            case 5:
                return getServerCitySarhangoblast(appCityId);
            case 6:
                return getServerCityAstrahanoblast(appCityId);
            case 8:
                return getServerCityBelgorodoblast(appCityId);
            case 9:
                return getServerCityBranskayaoblast(appCityId);
            case 10:
                return getServerCityByratia(appCityId);
            case 11:
                return getServerCityVladimiroblast(appCityId);
            case 12:
                return getServerCityVolgogradoblast(appCityId);
            case 13:
                return getServerCityVologodskayaoblast(appCityId);
            case 14:
                return getServerCityVoroneghskayaoblast(appCityId);
            case 15:
                return getServerCityDagestan(appCityId);
            case 16:
                return getServerCityEvreyskaiaoblast(appCityId);
            case 17:
                return getServerCityIvanovskayoblast(appCityId);
            case 18:
                return getServerCityIrkystskayobl(appCityId);
            case 19:
                return getServerCityKabardinobalc(appCityId);
            case 20:
                return getServerCityKaliningrobl(appCityId);
            case 21:
                return getServerCityKalmikia(appCityId);
            case 22:
                return getServerCityKalygskobl(appCityId);
            case 23:
                return getServerCityKamchatskkrai(appCityId);
            case 24:
                return getServerCityKarelia(appCityId);
            case 25:
                return getServerCityKemerovo(appCityId);
            case 26://ghjgj
                return getServerCityKirivskoblast(appCityId);
            case 27://ghjgj
                return getServerCityKomi(appCityId);
            case 28:
                return getServerCityKostrobl(appCityId);
            case 29:
                return getServerCityKrasndkrai(appCityId);
            case 30:
                return getServerCityKrasn(appCityId);
            case 31:
                return getServerCityKyrgan(appCityId);
            case 32:
                return getServerCityKyrsk(appCityId);
            case 33:
                return getServerCityLipeck(appCityId);
            case 34:
                return getServerCityMagadan(appCityId);
            case 35:
                return getServerCityMariial(appCityId);
            case 36:
                return getServerCityMordovia(appCityId);
            case 37:
                return getServerCityMyrmanskobl(appCityId);
            case 38:
                return getServerCityNighegorodsk(appCityId);
            case 39:
                return getServerCityVelikiinovgorod(appCityId);
            case 40:
                return getServerCityNovosib(appCityId);
            case 41:
                return getServerCityOmskayobl(appCityId);
            case 42:
                return getServerCityOren(appCityId);
            case 43:
                return getServerCityOrel(appCityId);
            case 44:
                return getServerCityPenza(appCityId);
            case 45:
                return getServerCityPerm(appCityId);
            case 46:
                return getServerCityPrimorskkrai(appCityId);
            case 47:
                return getServerCityPskov(appCityId);
            case 48:
                return getServerCityRostov(appCityId);
            case 49:
                return getServerCityrazanobl(appCityId);
            case 50:
                return getServerCitysamarskobl(appCityId);
            case 51:
                return getServerCitysaratovonl(appCityId);
            case 52:
                return getServerCitysaha(appCityId);
            case 53:
                return getServerCitysahalin(appCityId);
            case 54:
                return getServerCitysverdlovsk(appCityId);
            case 55:
                return getServerCitysevernosetia(appCityId);
            case 56:
                return getServerCitysmolobl(appCityId);
            case 57:
                return getServerCitystavropkrai(appCityId);
            case 58:
                return getServerCitytambovobl(appCityId);
            case 60:
                return getServerCitytverskobl(appCityId);
            case 61:
                return getServerCitiesTomsk(appCityId);
            case 62:
                return getServerCitytyva(appCityId);
            case 63:
                return getServerCitytylskobl(appCityId);
            case 64:
                return getServerCitytymen(appCityId);
            case 65:
                return getServerCityudmurtia(appCityId);
            case 66:
                return getServerCityylyanovskobl(appCityId);
            case 67:
                return getServerCityhabarovskoblst(appCityId);
            case 68:
                return getServerCityhakasia(appCityId);
            case 69:
                return getServerCityhantymansiiskAO(appCityId);
            case 70:
                return getServerCityChel(appCityId);
            case 71:
                return getServerCitychechna(appCityId);
            case 72:
                return getServerCitychyvashi(appCityId);
            case 73:
                return getServerCitychykodsk(appCityId);
            case 74:
                return getServerCityyamaloneneckiy(appCityId);
            case 75:
                return getServerCityyaroslavskobl(appCityId);
            case 7:
                return getServerCityBashkir(appCityId);
            case 59:
                return getServerCityTatar(appCityId);
            default:
                return 3345;
        }
    }

    public static int getAppCityId(int serverCityId){
        switch(region){
            case 0:
                return getAppCitiesMsk(serverCityId);
            case 1:
                return getAppCitiesSpb(serverCityId);
            case 2:
                return getAppCityAdygea(serverCityId);
            case 3:
                return getAppCityAltayskiykrai(serverCityId);
            case 4:
                return getAppCityAmyrskayoblast(serverCityId);
            case 5:
                return getAppCitySarhangoblast(serverCityId);
            case 6:
                return getAppCityAstrahanoblast(serverCityId);
            case 8:
                return getAppCityBelgorodoblast(serverCityId);
            case 9:
                return getAppCityBranskayaoblast(serverCityId);
            case 10:
                return getAppCityByratia(serverCityId);
            case 11:
                return getAppCityVladimiroblast(serverCityId);
            case 12:
                return getAppCityVolgogradoblast(serverCityId);
            case 13:
                return getAppCityVologodskayaoblast(serverCityId);
            case 14:
                return getAppCityVoroneghskayaoblast(serverCityId);
            case 15:
                return getAppCityDagestan(serverCityId);
            case 16:
                return getAppCityEvreyskaiaoblast(serverCityId);
            case 17:
                return getAppCityIvanovskayoblast(serverCityId);
            case 18:
                return getAppCityIrkystskayobl(serverCityId);
            case 19:
                return getAppCityKabardinobalc(serverCityId);
            case 20:
                return getAppCityKaliningrobl(serverCityId);
            case 21:
                return getAppCityKalmikia(serverCityId);
            case 22:
                return getAppCityKalygskobl(serverCityId);
            case 23:
                return getAppCityKamchatskkrai(serverCityId);
            case 24:
                return getAppCityKarelia(serverCityId);
            case 25:
                return getAppCityKemerovo(serverCityId);
            case 26://ghjgj
                return getAppCityKirivskoblast(serverCityId);
            case 27://ghjgj
                return getAppCityKomi(serverCityId);
            case 28:
                return getAppCityKostrobl(serverCityId);
            case 29:
                return getAppCityKrasndkrai(serverCityId);
            case 30:
                return getAppCityKrasn(serverCityId);
            case 31:
                return getAppCityKyrgan(serverCityId);
            case 32:
                return getAppCityKyrsk(serverCityId);
            case 33:
                return getAppCityLipeck(serverCityId);
            case 34:
                return getAppCityMagadan(serverCityId);
            case 35:
                return getAppCityMariial(serverCityId);
            case 36:
                return getAppCityMordovia(serverCityId);
            case 37:
                return getAppCityMyrmanskobl(serverCityId);
            case 38:
                return getAppCityNighegorodsk(serverCityId);
            case 39:
                return getAppCityVelikiinovgorod(serverCityId);
            case 40:
                return getAppCityNovosib(serverCityId);
            case 41:
                return getAppCityOmskayobl(serverCityId);
            case 42:
                return getAppCityOren(serverCityId);
            case 43:
                return getAppCityOrel(serverCityId);
            case 44:
                return getAppCityPenza(serverCityId);
            case 45:
                return getAppCityPerm(serverCityId);
            case 46:
                return getAppCityPrimorskkrai(serverCityId);
            case 47:
                return getAppCityPskov(serverCityId);
            case 48:
                return getAppCityRostov(serverCityId);
            case 49:
                return getAppCityrazanobl(serverCityId);
            case 50:
                return getAppCitysamarskobl(serverCityId);
            case 51:
                return getAppCitysaratovonl(serverCityId);
            case 52:
                return getAppCitysaha(serverCityId);
            case 53:
                return getAppCitysahalin(serverCityId);
            case 54:
                return getAppCitysverdlovsk(serverCityId);
            case 55:
                return getAppCitysevernosetia(serverCityId);
            case 56:
                return getAppCitysmolobl(serverCityId);
            case 57:
                return getAppCitystavropkrai(serverCityId);
            case 58:
                return getAppCitytambovobl(serverCityId);
            case 60:
                return getAppCitytverskobl(serverCityId);
            case 61:
                return getAppCitiesTomsk(serverCityId);
            case 62:
                return getAppCitytyva(serverCityId);
            case 63:
                return getAppCitytylskobl(serverCityId);
            case 64:
                return getAppCitytymen(serverCityId);
            case 65:
                return getAppCityudmurtia(serverCityId);
            case 66:
                return getAppCityylyanovskobl(serverCityId);
            case 67:
                return getAppCityhabarovskoblst(serverCityId);
            case 68:
                return getAppCityhakasia(serverCityId);
            case 69:
                return getAppCityhantymansiiskAO(serverCityId);
            case 70:
                return getAppCityChel(serverCityId);
            case 71:
                return getAppCitychechna(serverCityId);
            case 72:
                return getAppCitychyvashi(serverCityId);
            case 73:
                return getAppCitychykodsk(serverCityId);
            case 74:
                return getAppCityyamaloneneckiy(serverCityId);
            case 75:
                return getAppCityyaroslavskobl(serverCityId);
            case 7:
                return getAppCityBashkir(serverCityId);
            case 59:
                return getAppCityTatar(serverCityId);
            default:
                return 0;
        }
    }

    public static int getCityArrayId(int region){
        switch(region){
            case 0:
                GeoConvertIds.region=0;
                return R.array.citiesMsk;
            case 1:
                GeoConvertIds.region=1;
                return R.array.citiesSpb;
            case 2:
                GeoConvertIds.region=2;
                return R.array.citiesadygea;
            case 3:
                GeoConvertIds.region=3;
                return R.array.citiesAltayskiykrai;
            case 4:
                GeoConvertIds.region=4;
                return R.array.citiesamyrskayoblast;
            case 5:
                GeoConvertIds.region=5;
                return R.array.citiesarhangoblast;
            case 6:
                GeoConvertIds.region=6;
                return R.array.citiesastrahanoblast;
            case 8:
                GeoConvertIds.region=8;
                return R.array.citiesbelgorodoblast;
            case 9:
                GeoConvertIds.region=9;
                return R.array.citiesBranskayaoblast;
            case 10:
                GeoConvertIds.region=10;
                return R.array.citiesbyratia;
            case 11:
                GeoConvertIds.region=11;
                return R.array.citiesvladimiroblast;
            case 12:
                GeoConvertIds.region=12;
                return R.array.citiesvolgogradoblast;
            case 13:
                GeoConvertIds.region=13;
                return R.array.citiesvologodskayaoblast;
            case 14:
                GeoConvertIds.region=14;
                return R.array.citievoroneghskayaoblast;
            case 15:
                GeoConvertIds.region=15;
                return R.array.citiesdagestan;
            case 16:
                GeoConvertIds.region=16;
                return R.array.citiesevreyskaiaoblast;
            case 17:
                GeoConvertIds.region=17;
                return R.array.citiesivanovskayoblast;
            case 18:
                GeoConvertIds.region=18;
                return R.array.citiesirkystskayobl;
            case 19:
                GeoConvertIds.region=19;
                return R.array.citieskabardinobalc;
            case 20:
                GeoConvertIds.region=20;
                return R.array.citiesaliningrobl;
            case 21:
                GeoConvertIds.region=21;
                return R.array.citieskalmikia;
            case 22:
                GeoConvertIds.region=22;
                return R.array.citieskalygskobl;
            case 23:
                GeoConvertIds.region=23;
                return R.array.citieskamchatskkrai;
            case 24:
                GeoConvertIds.region=24;
                return R.array.citieskarelia;
            case 25:
                GeoConvertIds.region=25;
                return R.array.citieskemerovo;
            case 26:
                GeoConvertIds.region=26;
                return R.array.citieskirivskoblast;
            case 27:
                GeoConvertIds.region=27;
                return R.array.citieskomi;
            case 28:
                GeoConvertIds.region=28;
                return R.array.citieskostrobl;
            case 29:
                GeoConvertIds.region=29;
                return R.array.citieskrasndkrai;
            case 30:
                GeoConvertIds.region=30;
                return R.array.citieskrasn;
            case 31:
                GeoConvertIds.region=31;
                return R.array.citieskyrgan;
            case 32:
                GeoConvertIds.region=32;
                return R.array.citieskyrsk;
            case 33:
                GeoConvertIds.region=33;
                return R.array.citieslipeck;
            case 34:
                GeoConvertIds.region=34;
                return R.array.citiesmagadan;
            case 35:
                GeoConvertIds.region=35;
                return R.array.citiesmariial;
            case 36:
                GeoConvertIds.region=36;
                return R.array.citiesmordovia;
            case 37:
                GeoConvertIds.region=37;
                return R.array.citiesmyrmanskobl;
            case 38:
                GeoConvertIds.region=38;
                return R.array.citiesnighegorodsk;
            case 39:
                GeoConvertIds.region=39;
                return R.array.citiesvelikiinovgorod;
            case 40:
                GeoConvertIds.region=40;
                return R.array.citiesnovosib;
            case 41:
                GeoConvertIds.region=41;
                return R.array.citiesomskayobl;
            case 42:
                GeoConvertIds.region=42;
                return R.array.citiesoren;
            case 43:
                GeoConvertIds.region=43;
                return R.array.citiesorel;
            case 44:
                GeoConvertIds.region=44;
                return R.array.citiespenza;
            case 45:
                GeoConvertIds.region=45;
                return R.array.citiesPerm;
            case 46:
                GeoConvertIds.region=46;
                return R.array.citiesprimorskkrai;
            case 47:
                GeoConvertIds.region=47;
                return R.array.citiespskov;
            case 48:
                GeoConvertIds.region=48;
                return R.array.citiesrostov;
            case 49:
                GeoConvertIds.region=49;
                return R.array.citiesrazanobl;
            case 50:
                GeoConvertIds.region=50;
                return R.array.citiessamarskobl;
            case 51:
                GeoConvertIds.region=51;
                return R.array.citiessaratovonl;
            case 52:
                GeoConvertIds.region=52;
                return R.array.citiessaha;
            case 53:
                GeoConvertIds.region=53;
                return R.array.citiessahalin;
            case 54:
                GeoConvertIds.region=54;
                return R.array.citiessverdlovsk;
            case 55:
                GeoConvertIds.region=55;
                return R.array.citiesevernosetia;
            case 56:
                GeoConvertIds.region=56;
                return R.array.citiessmolobl;
            case 57:
                GeoConvertIds.region=57;
                return R.array.citiesstavropkrai;
            case 58:
                GeoConvertIds.region=58;
                return R.array.citietambovobl;
            case 60:
                GeoConvertIds.region=60;
                return R.array.citietverskobl;
            case 61:
                GeoConvertIds.region=61;
                return R.array.citietomskobl;
            case 62:
                GeoConvertIds.region=62;
                return R.array.citiestyva;
            case 63:
                GeoConvertIds.region=63;
                return R.array.citiestylskobl;
            case 64:
                GeoConvertIds.region=64;
                return R.array.citistymen;
            case 65:
                GeoConvertIds.region=65;
                return R.array.citiudmurtia;
            case 66:
                GeoConvertIds.region=66;
                return R.array.citisylyanovskobl;
            case 67:
                GeoConvertIds.region=67;
                return R.array.citishabarovskoblst;
            case 68:
                GeoConvertIds.region=68;
                return R.array.citishakasia;
            case 69:
                GeoConvertIds.region=69;
                return R.array.citihanty_mansiiskAO;
            case 70:
                GeoConvertIds.region=70;
                return R.array.citiesChel;
            case 71:
                GeoConvertIds.region=71;
                return R.array.citischechna;
            case 72:
                GeoConvertIds.region=72;
                return R.array.citichyvashi;
            case 73:
                GeoConvertIds.region=73;
                return R.array.citischykodsk;
            case 74:
                GeoConvertIds.region=74;
                return R.array.citisyamaloneneckiy;
            case 75:
                GeoConvertIds.region=75;
                return R.array.citisyaroslavskobl;
            case 59:
                GeoConvertIds.region=59;
                return R.array.citiesTatarstan;
            case 7:
                GeoConvertIds.region=7;
                return R.array.cities;
            default:
                return R.array.cities;
        }
    }

    public static int getServerCityTatar(int appCityId){
        switch(appCityId){
            case 0:
                return 5269;
            case 1:
                return 5266;
            case 2:
                return 5279;
            case 3:
                return 5281;
            case 4:
                return 5282;
            case 5:
                return 5290;
            default:
                return 5269;
        }
    }

    private static int getServerCityBashkir(int appCityId){
        switch(appCityId){
            case 0:
                return 3305;
            case 1:
                return 3306;
            case 2:
                return 3322;
            case 3:
                return 3329;
            case 4:
                return 3332;
            case 5:
                return 3335;
            case 6:
                return 3338;
            case 7:
                return 3343;
            case 8:
                return 3344;
            case 9:
                return 3345;
            case 10:
                return 3351;
            default:
                return 3345;
        }
    }

    private static int getAppCityTatar(int serverCityId){
        switch (serverCityId){
            case 5269:
                return 0;
            case 5266:
                return 1;
            case 5279:
                return 2;
            case 5281:
                return 3;
            case 5282:
                return 4;
            case 5290:
                return 5;
            default:
                return 0;
        }
    }

    private static int getAppCityBashkir(int serverCityId){
        switch (serverCityId){
            case 3305:
                return 0;
            case 3306:
                return 1;
            case 3322:
                return 2;
            case 3329:
                return 3;
            case 3332:
                return 4;
            case 3335:
                return 5;
            case 3338:
                return 6;
            case 3343:
                return 7;
            case 3344:
                return 8;
            case 3345:
                return 9;
            case 3351:
                return 10;
            default:
                return 9;
        }
    }

    private static int getServerCityPerm(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4720;
            default:
                return 4720;

        }

    }

    private static int getAppCityPerm(int serverCityId) {
        switch (serverCityId) {
            case 4720:
                return 0;
            default:
                return 0;
        }
    }

    private static int getAppCityChel(int serverCityId) {
        switch (serverCityId) {
            case 5539:
                return 0;
            case 5527:
                return 1;
            case 5528:
                return 2;
            case 5510:
                return 3;
            case 5538:
                return 4;
            default:
                return 0;
        }
    }

    private static int getServerCityChel(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5539;
            case 1:
                return 5527;
            case 2:
                return 5528;
            case 3:
                return 5510;
            case 4:
                return 5538;
            default:
                return 5539;
        }


    }

    private static int getAppCityAdygea(int serverCityId) {
        switch (serverCityId) {
            case 1998542:
                return 0;
            case 1998584:
                return 1;
            default:
                return 0;
        }

    }

    private static int getServerCityAdygea(int appCityId) {
        switch (appCityId) {
            case 0:
                return 1998542;
            case 1:
                return 1998584;
            default:
                return 1998542;
        }
    }

    private static int getAppCityAltayskiykrai(int serverCityId) {
        switch (serverCityId) {
            case 3166:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityAltayskiykra(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3166;
            default:
                return 3166;
        }

    }

    private static int getAppCityAmyrskayoblast(int serverCityId) {
        switch (serverCityId) {
            case 3227:
                return 0;
            case 3242:
                return 1;
            default:
                return 0;
        }
    }

    private static int getServerCityAmyrskayoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3227;
            case 1:
                return 3242;
            default:
                return 3227;
        }
    }

    private static int getAppCitySarhangoblast(int serverCityId) {
        switch (serverCityId) {
            case 3253:
                return 0;
            case 3267:
                return 1;
            default:
                return 0;
        }
    }

    private static int getServerCitySarhangoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3253;
            case 1:
                return 3267;
            default:
                return 3253;
        }
    }

    private static int getAppCityAstrahanoblast(int serverCityId) {
        switch (serverCityId) {
            case 3283:
                return 0;
            case 3284:
                return 1;
            case 3289:
                return 2;
            // не хватает город: Нуриманов
            default:
                return 0;
        }
    }

    private static int getServerCityAstrahanoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3283;
            case 1:
                return 3284;
            case 2:
                return 3289;
            // не хватает город: Нуриманов
            default:
                return 3283;
        }
    }

    private static int getAppCityBelgorodoblast(int serverCityId) {
        switch (serverCityId) {
            case 3354:
                return 0;
            case 3364:
                return 1;
            default:
                return 0;
        }
    }

    private static int getServerCityBelgorodoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3354;
            case 1:
                return 3364;
            default:
                return 3354;
        }
    }

    private static int getAppCityBranskayaoblast(int serverCityId) {
        switch (serverCityId) {
            case 3376:
                return 0;
            case 3389:
                return 1;
            case 3374:
                return 2;
            case 3405:
                return 3;
            // не хватает городов: Ляличи, Юдиново
            default:
                return 0;
        }
    }

    private static int getServerCityBranskayaoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3376;
            case 1:
                return 3389;
            case 2:
                return 3374;
            case 3:
                return 3405;
            // не хватает городов: Ляличи, Юдиново
            default:
                return 3376;
        }
    }

    private static int getAppCityByratia(int serverCityId) {
        switch (serverCityId) {
            case 3435:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityByratia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3435;
            default:
                return 3435;
        }
    }

    private static int getAppCityVladimiroblast(int serverCityId) {
        switch (serverCityId) {
            case 3446:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityVladimiroblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3446;
            default:
                return 3446;
        }
    }

    private static int getAppCityVolgogradoblast(int serverCityId) {
        switch (serverCityId) {
            case 3472:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityVolgogradoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3472;
            default:
                return 3472;
        }
    }

    private static int getAppCityVologodskayaoblast(int serverCityId) {
        switch (serverCityId) {
            case 3509:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityVologodskayaoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3509;
            default:
                return 3509;
        }
    }

    private static int getAppCityVoroneghskayaoblast(int serverCityId) {
        switch (serverCityId) {
            case 3538:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityVoroneghskayaoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3538;
            default:
                return 3538;
        }
    }

    private static int getAppCityDagestan(int serverCityId) {
        switch (serverCityId) {
            case 3658:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityDagestan(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3658;
            default:
                return 3658;
        }
    }

    private static int getAppCityEvreyskaiaoblast(int serverCityId) {
        switch (serverCityId) {
            case 3674:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityEvreyskaiaoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3674;
            default:
                return 3674;
        }
    }

    private static int getAppCityIvanovskayoblast(int serverCityId) {
        switch (serverCityId) {
            case 3684:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityIvanovskayoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3684;
            default:
                return 3684;
        }
    }

    private static int getAppCityIrkystskayobl(int serverCityId) {
        switch (serverCityId) {
            case 3731:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityIrkystskayobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3731;
            default:
                return 3731;
        }
    }

    private static int getAppCityKabardinobalc(int serverCityId) {
        switch (serverCityId) {
            case 3754:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKabardinobalc(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3754;
            default:
                return 3754;
        }
    }

    private static int getAppCityKaliningrobl(int serverCityId) {
        switch (serverCityId) {
            case 3770:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKaliningrobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3770;
            default:
                return 3770;
        }
    }

    private static int getAppCityKalmikia(int serverCityId) {
        switch (serverCityId) {
            case 3837:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKalmikia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3837;
            default:
                return 3837;
        }
    }

    private static int getAppCityKalygskobl(int serverCityId) {
        switch (serverCityId) {
            case 3854:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKalygskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3854;
            default:
                return 3854;
        }
    }

    private static int getAppCityKamchatskkrai(int serverCityId) {
        switch (serverCityId) {
            case 3887:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKamchatskkrai(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3887;
            default:
                return 3887;
        }
    }

    private static int getAppCityKarelia(int serverCityId) {
        switch (serverCityId) {
            case 3912:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKarelia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3912;
            default:
                return 3912;
        }
    }

    private static int getAppCityKemerovo(int serverCityId) {
        switch (serverCityId) {
            case 3933:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKemerovo(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3933;
            default:
                return 3933;
        }
    }

    private static int getAppCityKirivskoblast(int serverCityId) {
        switch (serverCityId) {
            case 3963:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKirivskoblast(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3963;
            default:
                return 3963;
        }
    }

    private static int getAppCityKomi(int serverCityId) {
        switch (serverCityId) {
            case 4019:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKomi(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4019;
            default:
                return 4019;
        }
    }

    private static int getAppCityKostrobl(int serverCityId) {
        switch (serverCityId) {
            case 4036:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKostrobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4036;
            default:
                return 4036;
        }
    }

    private static int getAppCityKrasndkrai(int serverCityId) {
        switch (serverCityId) {
            case 4079:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKrasndkrai(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4079;
            default:
                return 4079;
        }
    }

    private static int getAppCityKrasn(int serverCityId) {
        switch (serverCityId) {
            case 4149:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKrasn(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4149;
            default:
                return 4149;
        }
    }

    private static int getAppCityKyrgan(int serverCityId) {
        switch (serverCityId) {
            case 4183:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKyrgan(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4183;
            default:
                return 4183;
        }
    }

    private static int getAppCityKyrsk(int serverCityId) {
        switch (serverCityId) {
            case 4210:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityKyrsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4210;
            default:
                return 4210;
        }
    }

    private static int getAppCityLipeck(int serverCityId) {
        switch (serverCityId) {
            case 4238:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityLipeck(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4238;
            default:
                return 4238;
        }
    }

    private static int getAppCityMagadan(int serverCityId) {
        switch (serverCityId) {
            case 4254:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityMagadan(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4254;
            default:
                return 4254;
        }
    }

    private static int getAppCityMariial(int serverCityId) {
        switch (serverCityId) {
            case 4274:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityMariial(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4274;
            default:
                return 4274;
        }
    }

    private static int getAppCityMordovia(int serverCityId) {
        switch (serverCityId) {
            case 4306:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityMordovia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4306;
            default:
                return 4306;
        }
    }

    private static int getAppCityMyrmanskobl(int serverCityId) {
        switch (serverCityId) {
            case 4496:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityMyrmanskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4496;
            default:
                return 4496;
        }
    }

    private static int getAppCityNighegorodsk(int serverCityId) {
        switch (serverCityId) {
            case 3612:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityNighegorodsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3612;
            default:
                return 3612;
        }
    }

    private static int getAppCityVelikiinovgorod(int serverCityId) {
        switch (serverCityId) {
            case 4517:
                return 0;
            default:
               return 0;
        //Не хватает город: Великий Новгород - решено
        }
    }

    private static int getServerCityVelikiinovgorod(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4517;
            default:
                return 4517;
        //Не хватает город: Великий Новгород - решено
        }
    }

    private static int getAppCityNovosib(int serverCityId) {
        switch (serverCityId) {
            case 4549:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityNovosib(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4549;
            default:
                return 4549;
        }
    }

    private static int getAppCityOmskayobl(int serverCityId) {
        switch (serverCityId) {
            case 4580:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityOmskayobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4580;
            default:
                return 4580;
        }
    }

    private static int getAppCityOren(int serverCityId) {
        switch (serverCityId) {
            case 4617:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityOren(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4617;
            default:
                return 4617;
        }
    }

    private static int getAppCityOrel(int serverCityId) {
        switch (serverCityId) {
            case 4650:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityOrel(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4650;
            default:
                return 4650;
        }
    }

    private static int getAppCityPenza(int serverCityId) {
        switch (serverCityId) {
            case 4682:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityPenza(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4682;
            default:
                return 4682;
        }
    }

    private static int getAppCityPrimorskkrai(int serverCityId) {
        switch (serverCityId) {
            case 4741:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityPrimorskkrai(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4741;
            default:
                return 4741;
        }
    }

    private static int getAppCityPskov(int serverCityId) {
        switch (serverCityId) {
            case 4793:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityPskov(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4793;
            default:
                return 4793;
        }
    }

    private static int getAppCityRostov(int serverCityId) {
        switch (serverCityId) {
            case 4848:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityRostov(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4848;
            default:
                return 4848;
        }
    }

    private static int getAppCityrazanobl(int serverCityId) {
        switch (serverCityId) {
            case 4879:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityrazanobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4879;
            default:
                return 4879;
        }
    }

    private static int getAppCitysamarskobl(int serverCityId) {
        switch (serverCityId) {
            case 4917:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysamarskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4917;
            default:
                return 4917;
        }
    }

    private static int getAppCitysaha(int serverCityId) {
        switch (serverCityId) {
            case 5051:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysaha(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5051;
            default:
                return 5051;
        }
    }

    private static int getAppCitysahalin(int serverCityId) {
        switch (serverCityId) {
            case 5079:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysahalin(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5079;
            default:
                return 5079;
        }
    }

    private static int getAppCitysaratovonl(int serverCityId) {
        switch (serverCityId) {
            case 5005:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysaratovonl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5005;
            default:
                return 5005;
        }
    }

    private static int getAppCitysverdlovsk(int serverCityId) {
        switch (serverCityId) {
            case 5106:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysverdlovsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5106;
            default:
                return 5106;
        }
    }

    private static int getAppCitysevernosetia(int serverCityId) {
        switch (serverCityId) {
            case 5156:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysevernosetia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5156;
            default:
                return 5156;
        }
    }

    private static int getAppCitysmolobl(int serverCityId) {
        switch (serverCityId) {
            case 5184:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitysmolobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5184;
            default:
                return 5184;
        }
    }

    private static int getAppCitystavropkrai(int serverCityId) {
        switch (serverCityId) {
            case 5219:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitystavropkrai(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5219;
            default:
                return 5219;
        }
    }

    private static int getAppCitytambovobl(int serverCityId) {
        switch (serverCityId) {
            case 5242:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitytambovobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5242;
            default:
                return 5242;
        }
    }

    private static int getAppCitytverskobl(int serverCityId) {
        switch (serverCityId) {
            case 3823:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitytverskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 3823;
            default:
                return 3823;
        }
    }

    private static int getAppCitytyva(int serverCityId) {
        switch (serverCityId) {
            case 5315:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitytyva(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5315;
            default:
                return 5315;
        }
    }

    private static int getAppCitytylskobl(int serverCityId) {
        switch (serverCityId) {
            case 5352:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitytylskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5352;
            default:
                return 5352;
        }
    }

    private static int getAppCitytymen(int serverCityId) {
        switch (serverCityId) {
            case 5395:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitytymen(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5395;
            default:
                return 5395;
        }
    }

    private static int getAppCityudmurtia(int serverCityId) {
        switch (serverCityId) {
            case 5414:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityudmurtia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5414;
            default:
                return 5414;
        }
    }

    private static int getAppCityylyanovskobl(int serverCityId) {
        switch (serverCityId) {
            case 5456:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityylyanovskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5456;
            default:
                return 5456;
        }
    }

    private static int getAppCityhabarovskoblst(int serverCityId) {
        switch (serverCityId) {
            case 5504:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityhabarovskoblst(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5504;
            default:
                return 5504;
        }
    }

    private static int getAppCityhakasia(int serverCityId) {
        switch (serverCityId) {
            case 4093692:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityhakasia(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4093692;
            default:
                return 4093692;
        }
    }

    private static int getAppCityhantymansiiskAO(int serverCityId) {
        switch (serverCityId) {
            case 7718652:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityhantymansiiskAO(int appCityId) {
        switch (appCityId) {
            case 0:
                return 7718652;
            default:
                return 7718652;
        }
    }

    private static int getAppCitychechna(int serverCityId) {
        switch (serverCityId) {
            case 5545:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitychechna(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5545;
            default:
                return 5545;
        }
    }

    private static int getAppCitychyvashi(int serverCityId) {
        switch (serverCityId) {
            case 5619:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitychyvashi(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5619;
            default:
                return 5619;
        }
    }

    private static int getAppCitychykodsk(int serverCityId) {
        switch (serverCityId) {
            case 2415620:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitychykodsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 2415620;
            default:
                return 2415620;
        }
    }

    private static int getAppCityyamaloneneckiy(int serverCityId) {
        switch (serverCityId) {
            case 5019404:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityyamaloneneckiy(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5019404;
            default:
                return 5019404;
        }
    }

    private static int getAppCityyaroslavskobl(int serverCityId) {
        switch (serverCityId) {
            case 5646:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCityyaroslavskobl(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5646;
            default:
                return 5646;
        }
    }

    private static int getAppCitiesMsk(int serverCityId){
        switch(serverCityId){
            case 4400:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitiesMsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4400;
            default:
                return 4400;
        }
    }

    private static int getAppCitiesSpb(int serverCityId){
        switch(serverCityId){
            case 4962:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitiesSpb(int appCityId) {
        switch (appCityId) {
            case 0:
                return 4962;
            default:
                return 4400;
        }
    }

    private static int getAppCitiesTomsk(int serverCityId){
        switch(serverCityId){
            case 5310:
                return 0;
            default:
                return 0;
        }
    }

    private static int getServerCitiesTomsk(int appCityId) {
        switch (appCityId) {
            case 0:
                return 5310;
            default:
                return 5310;
        }
    }
}
