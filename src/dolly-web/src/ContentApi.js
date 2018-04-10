const uri = "http://localhost:3050";

class ContentApi {

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


