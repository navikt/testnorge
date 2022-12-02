import React from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import { ORGANISASJONSTYPE_TOGGLE } from '~/components/fagsystem/inntektstub/form/partials/orgnummerToggle'

interface OrganisasjonToogleGruppeProps {
	inputType: string
	handleToggleChange: (value: string) => void
}

export const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

export const OrganisasjonToogleGruppe = ({ handleToggleChange }: OrganisasjonToogleGruppeProps) => {
	return (
		<ToggleGroup
			size={'small'}
			onChange={handleToggleChange}
			defaultValue={sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe}
			style={{ margin: '5px 0 5px' }}
		>
			<ToggleGroup.Item key={inputValg.fraEgenListe} value={inputValg.fraEgenListe}>
				Egen organisasjon
			</ToggleGroup.Item>
			<ToggleGroup.Item key={inputValg.fraFellesListe} value={inputValg.fraFellesListe}>
				Felles organisasjoner
			</ToggleGroup.Item>
			<ToggleGroup.Item key={inputValg.skrivSelv} value={inputValg.skrivSelv}>
				Skriv inn org.nr.
			</ToggleGroup.Item>
		</ToggleGroup>
	)
}
