import { ToggleGroup } from '@navikt/ds-react'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'

interface OrganisasjonToggleGruppeProps {
	inputType: string
	handleToggleChange: (value: string) => void
}

export const OrganisasjonToggleGruppe = ({
	inputType,
	handleToggleChange,
}: OrganisasjonToggleGruppeProps) => {
	return (
		<ToggleGroup
			size={'small'}
			onChange={handleToggleChange}
			value={inputType}
			style={{ margin: '5px 0 10px 0' }}
		>
			<ToggleGroup.Item key={ArbeidsgiverTyper.felles} value={ArbeidsgiverTyper.felles}>
				Felles organisasjoner
			</ToggleGroup.Item>
			<ToggleGroup.Item key={ArbeidsgiverTyper.egen} value={ArbeidsgiverTyper.egen}>
				Egen organisasjon
			</ToggleGroup.Item>
			<ToggleGroup.Item key={ArbeidsgiverTyper.fritekst} value={ArbeidsgiverTyper.fritekst}>
				Skriv inn org.nr.
			</ToggleGroup.Item>
		</ToggleGroup>
	)
}
