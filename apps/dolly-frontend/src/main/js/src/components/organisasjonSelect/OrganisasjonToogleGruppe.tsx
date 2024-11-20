import { ToggleGroup } from '@navikt/ds-react'
import { useEffect } from 'react'
import { ORGANISASJONSTYPE_TOGGLE } from '@/components/fagsystem/utils'

interface OrganisasjonToogleGruppeProps {
	inputType: string
	handleToggleChange: (value: string) => void
}

export const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

export const OrganisasjonToogleGruppe = ({ handleToggleChange }: OrganisasjonToogleGruppeProps) => {
	// TODO: bruk samme toggle som arb.for.??
	return (
		<ToggleGroup
			size={'small'}
			onChange={handleToggleChange}
			defaultValue={sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe}
			// value={sessionStorage.getItem(ORGANISASJONSTYPE_TOGGLE) || inputValg.fraFellesListe}
			style={{ margin: '5px 0 5px', backgroundColor: '#ffffff' }}
		>
			<ToggleGroup.Item key={inputValg.fraFellesListe} value={inputValg.fraFellesListe}>
				Felles organisasjoner
			</ToggleGroup.Item>
			<ToggleGroup.Item key={inputValg.fraEgenListe} value={inputValg.fraEgenListe}>
				Egen organisasjon
			</ToggleGroup.Item>
			<ToggleGroup.Item key={inputValg.skrivSelv} value={inputValg.skrivSelv}>
				Skriv inn org.nr.
			</ToggleGroup.Item>
		</ToggleGroup>
	)
}
