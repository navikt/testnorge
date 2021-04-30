import React, { useEffect, useState } from "react";
import Profil from "./Profil";
import ProfilService from "@/services/ProfilService";

export default () => {
  const [navn, setNavn] = useState<string>();
  const [bilde, setBilde] = useState<Response>();

  useEffect(() => {
    ProfilService.fetchProfil().then(({ visningsNavn }) =>
      setNavn(visningsNavn)
    );
    ProfilService.fetchBilde().then((response) => setBilde(response));
  }, []);

  return <Profil visningsnavn={navn} profilbilde={bilde} />;
};
