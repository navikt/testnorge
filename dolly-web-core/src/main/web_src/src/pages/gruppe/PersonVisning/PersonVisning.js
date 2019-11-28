import React from 'react'
import { useMount } from 'react-use'
import { TidligereBestillinger } from './TidligereBestillinger/TidligereBestillinger'
import { TpsfVisning } from '~/components/fagsystem/tpsf/visning/Visning'
import { KrrVisning } from '~/components/fagsystem/krrstub/visning/KrrVisning'
import { PdlfVisning } from '~/components/fagsystem/pdlf/visning/Visning'
import Button from '~/components/ui/button/Button'

import './PersonVisning.less'

export const PersonVisning = ({ getDataFraFagsystemer, data, testIdent, bestilling, loading }) => {
	useMount(getDataFraFagsystemer)

	data.pdlforvalter = {
		kontaktinformasjonForDoedsbo: [
			{
				skifteform: 'OFFENTLIG',
				attestutstedelsesdato: '2015-03-25',
				personSomKontakt: {
					foedselsdato: '2015-03-25',
					personnavn: {
						fornavn: 'Johnny',
						mellomnavn: 'Testus',
						etternavn: 'Bravo'
					},
					identifikasjonsnummer: '12345678901'
				},
				adresse: {
					adresselinje1: 'Eksempelveien 1234A',
					adresselinje2: 'Eksempelveien 5678B',
					poststedsnavn: 'Westeros',
					postnummer: '5432',
					landkode: 'NOR'
				}
			},
			{
				skifteform: 'OFFENTLIG',
				attestutstedelsesdato: '2015-03-25',
				advokatSomKontakt: {
					organisasjonsnavn: 'Fantasifabrikken',
					organisasjonsnummer: '87654321',
					personnavn: {
						fornavn: 'Johnny',
						mellomnavn: 'Testus',
						etternavn: 'Bravo'
					}
				},
				adresse: {
					adresselinje1: 'Eksempelveien 1234A',
					adresselinje2: 'Eksempelveien 5678B',
					poststedsnavn: 'Westeros',
					postnummer: '5432',
					landkode: 'NOR'
				}
			},
			{
				skifteform: 'OFFENTLIG',
				attestutstedelsesdato: '2015-03-25',
				organisasjonSomKontakt: {
					organisasjonsnavn: 'Evilcorp INC',
					organisasjonsnummer: '87654321',
					kontaktperson: {
						fornavn: 'Johnny',
						mellomnavn: 'Testus',
						etternavn: 'Bravo'
					}
				},
				adresse: {
					adresselinje1: 'Eksempelveien 1234A',
					adresselinje2: 'Eksempelveien 5678B',
					poststedsnavn: 'Westeros',
					postnummer: '5432',
					landkode: 'NOR'
				}
			}
		]
	}

	return (
		<div className="person-visning">
			<TpsfVisning data={TpsfVisning.filterValues(data.tpsf, bestilling.tpsfKriterier)} />
			<PdlfVisning data={data.pdlforvalter} loading={loading.pdlforvalter} />

			{/* <SigrunVisning /> */}
			<KrrVisning data={data.krrstub} loading={loading.krrstub} />
			{/* <AaregVisning /> */}
			{/* <InstVisning /> */}
			{/* <ArenaVisning /> */}
			{/* <UdiVisning /> */}
			<TidligereBestillinger ids={testIdent.bestillingId} />
			<div className="flexbox--align-center--justify-end">
				<Button className="flexbox--align-center" kind="details">
					BESTILLINGSDETALJER
				</Button>
				<Button className="flexbox--align-center" kind="edit">
					REDIGER
				</Button>

				{/* Slett kan være modal med egen komponent */}
				<Button className="flexbox--align-center" kind="trashcan">
					SLETT
				</Button>
			</div>
		</div>
	)
}
