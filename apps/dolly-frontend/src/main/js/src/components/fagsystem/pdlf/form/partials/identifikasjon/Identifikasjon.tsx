import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, usePanelError } from '@/components/ui/form/formUtils'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FalskIdentitet } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/falskIdentitet/FalskIdentitet'
import { UtenlandsId } from '@/components/fagsystem/pdlf/form/partials/identifikasjon/utenlandsId/UtenlandsId'
import { NyIdent } from '@/components/fagsystem/pdlf/form/partials/nyIdent/nyIdent'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface IdentifikasjonValues {
	formMethods: UseFormReturn
}

export const identifikasjonAttributter = [
	'pdldata.person.falskIdentitet',
	'pdldata.person.utenlandskIdentifikasjonsnummer',
	'pdldata.person.nyident',
]

export const Identifikasjon = ({ formMethods }: IdentifikasjonValues) => {
	return (
		<Vis attributt={identifikasjonAttributter}>
			<Panel
				heading="Identifikasjon"
				hasErrors={usePanelError(identifikasjonAttributter)}
				iconType="identifikasjon"
				startOpen={erForsteEllerTest(formMethods.getValues(), identifikasjonAttributter)}
			>
				<Kategori title="Falsk identitet" vis="pdldata.person.falskIdentitet">
					<FalskIdentitet formMethods={formMethods} />
				</Kategori>
				<Kategori
					title="Utenlandsk identifikasjonsnummer"
					vis="pdldata.person.utenlandskIdentifikasjonsnummer"
					flex={false}
				>
					<UtenlandsId />
				</Kategori>
				<Kategori title="Ny identitet" vis="pdldata.person.nyident">
					<NyIdent formMethods={formMethods} />
				</Kategori>
			</Panel>
		</Vis>
	)
}
