import { ToggleGroup } from '@navikt/ds-react'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'

interface OrganisasjonToogleGruppeProps {
	inputType: string
	handleToggleChange: (value: string) => void
}

export const OrganisasjonToogleGruppe = ({
	inputType,
	handleToggleChange,
}: OrganisasjonToogleGruppeProps) => {
	// TODO: bruk samme toggle som arb.for.?? Nei, men kanskje legge denne i utils?
	return (
		<ToggleGroup
			size={'small'}
			onChange={handleToggleChange}
			value={inputType}
			style={{ margin: '5px 0 5px', backgroundColor: '#ffffff' }}
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
