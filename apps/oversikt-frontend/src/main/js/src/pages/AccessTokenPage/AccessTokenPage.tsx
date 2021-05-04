import React from "react";
import PageWithMenu from "@/components/PageWithMenu";
import FetchAccessToken from "@/components/FetchAccessToken";
import { useParams } from "react-router-dom";
import { Page } from "@navikt/dolly-komponenter";

type Props = {
  navigations: Navigation[];
};

const AccessTokenPage = ({ navigations }: Props) => {
  // @ts-ignore
  const { name } = useParams();
  return (
    <PageWithMenu navigations={navigations} menuTitle="Applikasjoner">
      <FetchAccessToken
        name={name}
        labels={{
          header: "Access Token",
          subHeader: `Generer token for ${name}`,
          description: `Token som kan bruke til å logge på ${name}.`,
        }}
      />
    </PageWithMenu>
  );
};

export default AccessTokenPage;
