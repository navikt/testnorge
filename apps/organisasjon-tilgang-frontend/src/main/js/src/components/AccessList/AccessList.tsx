import React from 'react';
import { LoadableComponent } from '@navikt/dolly-komponenter';
import { OrganisasjonTilgangService } from '@/services';
import 'nav-frontend-tabell-style';
import { Fareknapp } from 'nav-frontend-knapper';
import ErrorBoundary from 'nav-datovelger/lib/common/ErrorBoundary';

const format = (date: Date) => date.toLocaleDateString() + ' ' + date.toLocaleTimeString();

const AccessList = () => (
  <div>
    <h2>Organisasjoner som har tilgang til Dolly</h2>
    <ErrorBoundary>
      <LoadableComponent
        onFetch={OrganisasjonTilgangService.getOrganisasjonTilganger}
        render={(list) => (
          <table className="tabell">
            <thead>
              <tr>
                <th>Org.nr</th>
                <th>Navn</th>
                <th>Form</th>
                <th>Gyldig Til</th>
                <th>Fjern tilgang</th>
              </tr>
            </thead>
            <tbody>
              {list.map((item, index) => (
                <tr key={index}>
                  <td>{item.orgnisasjonsnummer}</td>
                  <td>{item.navn}</td>
                  <td>{item.orgnisasjonsfrom}</td>
                  <td>{format(new Date(item.gyldigTil))}</td>
                  <td>
                    <Fareknapp
                      onClick={(event) => {
                        event.preventDefault();
                        if (window.confirm('Er du sikker pa at du ønsker å slette tilgangen?')) {
                          OrganisasjonTilgangService.deleteOrganisasjonTilgang(
                            item.orgnisasjonsnummer
                          ).then(() => {
                            window.location.reload();
                          });
                        }
                      }}
                    >
                      Fjern
                    </Fareknapp>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      />
    </ErrorBoundary>
  </div>
);

AccessList.displayName = 'AccessList';

export default AccessList;
