import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Innvandring } from '@/components/fagsystem/pdlf/visning/partials/Innvandring'
import { Utvandring } from '@/components/fagsystem/pdlf/visning/partials/Utvandring'
import { PersonData } from '@/components/fagsystem/pdlf/PdlTypes'
import { Statsborgerskap } from '@/components/fagsystem/pdlf/visning/partials/Statsborgerskap'

type NasjonalitetTypes = {
	data: PersonData
	tmpPersoner?: Array<PersonData>
	visTittel?: boolean
	erPdlVisning?: boolean
	identtype?: string
	erRedigerbar?: boolean
}

export const Nasjonalitet = ({
	data,
	tmpPersoner,
	visTittel = true,
	erPdlVisning = false,
	identtype,
	erRedigerbar = true,
}: NasjonalitetTypes) => {
	if (!data) {
		return null
	}

	const { statsborgerskap, innflytting, utflytting, ident } = data

	if (!statsborgerskap && !innflytting && !utflytting) {
		return null
	}
	if (statsborgerskap?.length < 1 && innflytting?.length < 1 && utflytting?.length < 1) {
		return null
	}

	return (
		<div>
			{visTittel && <SubOverskrift label="Nasjonalitet" iconKind="nasjonalitet" />}
			{statsborgerskap?.length > 0 && (
				<Statsborgerskap
					data={statsborgerskap}
					tmpPersoner={tmpPersoner}
					ident={ident}
					erPdlVisning={erPdlVisning}
					identtype={identtype}
					erRedigerbar={erRedigerbar}
				/>
			)}
			{innflytting?.length > 0 && (
				<Innvandring
					data={innflytting}
					utflyttingData={utflytting}
					tmpPersoner={tmpPersoner}
					ident={ident}
					erPdlVisning={erPdlVisning}
					erRedigerbar={erRedigerbar}
				/>
			)}
			{utflytting?.length > 0 && (
				<Utvandring
					data={utflytting}
					innflyttingData={innflytting}
					tmpPersoner={tmpPersoner}
					ident={ident}
					erPdlVisning={erPdlVisning}
					erRedigerbar={erRedigerbar}
				/>
			)}
		</div>
	)
}