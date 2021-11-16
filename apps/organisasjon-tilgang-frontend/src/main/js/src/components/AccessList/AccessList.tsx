import React from 'react';
import { LoadableComponent } from '@navikt/dolly-komponenter';
import { OrganisasjonTilgangService } from '@/services';
import 'nav-frontend-tabell-style';
import { Fareknapp } from 'nav-frontend-knapper';
import ErrorBoundary from 'nav-datovelger/lib/common/ErrorBoundary';

const AccessList = () => (
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
            {list.map((item) => (
              <tr>
                <td>{item.orgnisasjonsnummer}</td>
                <td>{item.navn}</td>
                <td>{item.orgnisasjonsfrom}</td>
                <td>
                  {new Date(item.gyldigTil).toLocaleDateString() +
                    ' ' +
                    new Date(item.gyldigTil).toLocaleTimeString()}
                </td>
                <td>
                  <Fareknapp
                    onClick={(event) => {
                      event.preventDefault();
                      if (window.confirm('Er du sikker pa at du ønsker å slette tilgangen?')) {
                        console.log('OK');
                      } else {
                        console.log('cancel');
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
);

AccessList.displayName = 'AccessList';

export default AccessList;
