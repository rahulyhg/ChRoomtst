package unicorn.ertech.chroom;

/**
 * Created by UNICORN on 06.04.2015.
 */
public class GeoConvertIds {
    int region;

    public int getServerRegionId(int appRegionId){
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

    public int getAppRegionId(int serverRegionId){
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


}
