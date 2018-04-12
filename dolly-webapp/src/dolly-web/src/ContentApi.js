const uri = "http://localhost:3050";

class ContentApi {

    static getGrupper(){
        return uri + "/grupper";
    }

    static getGruppe(id){
        return uri + "/gruppe/id";
    }

    static putGruppe(id){
        return uri + "/gruppe/id";
    }

    static getPersons(){
        return uri + "/persons";
    }

    static postPersons(){
        return uri + "/persons";
    }

    static putPersons(id){
        return uri + "/persons/" + id;
    }

}

export default ContentApi;


