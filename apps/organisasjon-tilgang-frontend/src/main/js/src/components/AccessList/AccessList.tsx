import React from 'react';
import { Knapp, LoadableComponent } from '@navikt/dolly-komponenter';
import { OrganisasjonTilgangService } from '@/services';

const format = (date: Date) => date.toLocaleDateString() + ' ' + date.toLocaleTimeString();

const AccessList = () => (
  <div>
    <h2>Organisasjoner som har tilgang til Dolly</h2>
    <LoadableComponent
      onFetch={OrganisasjonTilgangService.getOrganisasjonTilganger}
      render={(list) => (
        <table className="tabell">
          <thead>
            <tr>
              <th>Org.nr</th>
              <th>Navn</th>
              <th>Form</th>
              <th>Miljø</th>
              <th>Gyldig til</th>
              <th>Fjern tilgang</th>
            </tr>
          </thead>
          <tbody>
            {list.map((item, index) => (
              <tr key={index}>
                <td>{item.organisasjonsnummer}</td>
                <td>{item.navn}</td>
                <td>{item.organisasjonsform}</td>
                <td>{item.miljoe}</td>
                <td>{format(new Date(item.gyldigTil))}</td>
                <td>
                  <Knapp
                    variant={'danger'}
                    onClick={(event: { preventDefault: () => void }) => {
                      event.preventDefault();
                      if (window.confirm('Er du sikker pa at du ønsker å slette tilgangen?')) {
                        OrganisasjonTilgangService.deleteOrganisasjonTilgang(
                          item.organisasjonsnummer
                        ).then(() => {
                          window.location.reload();
                        });
                      }
                    }}
                  >
                    Fjern
                  </Knapp>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    />
  </div>
);

AccessList.displayName = 'AccessList';

export default AccessList;
