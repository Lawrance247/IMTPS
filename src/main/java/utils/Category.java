package utils;

public enum Category 
{
    ACADEMIC{
        public String getName(){
            return "academic";
        }
    },
    FAMILY{
        public String getName(){
            return "family";
        }
    },
    HEALTH{
        public String getName(){
            return "health";
        }
    },
    LEISURE{
        public String getName(){
            return "leisure";
        }
    }
    
}