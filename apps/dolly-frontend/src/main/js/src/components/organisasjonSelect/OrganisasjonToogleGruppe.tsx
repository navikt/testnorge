import React from 'react'
import { ToggleGruppe, ToggleKnapp } from '~/components/ui/toggle/Toggle'

interface OrganisasjonToogleGruppeProps {
	path: string
	inputType: string
	handleToggleChange: (event: React.ChangeEvent<any>) => void
}

export const inputValg = { fraEgenListe: 'egen', fraFellesListe: 'felles', skrivSelv: 'skriv' }

export const OrganisasjonToogleGruppe = ({
	path,
	inputType,
	handleToggleChange,
}: OrganisasjonToogleGruppeProps) => {
	return (
		<ToggleGruppe onChange={handleToggleChange} name={path}>
			<ToggleKnapp
				key={inputValg.fraFellesListe}
				value={inputValg.fraFellesListe}
				checked={inputType === inputValg.fraFellesListe}
			>
				Felles organisasjoner
			</ToggleKnapp>
			<ToggleKnapp
				key={inputValg.fraEgenListe}
				value={inputValg.fraEgenListe}
				checked={inputType === inputValg.fraEgenListe}
			>
				Egen organisasjon
			</ToggleKnapp>
			<ToggleKnapp
				key={inputValg.skrivSelv}
				value={inputValg.skrivSelv}
				checked={inputType === inputValg.skrivSelv}
			>
				Skriv inn org.nr.
			</ToggleKnapp>
		</ToggleGruppe>
	)
}
