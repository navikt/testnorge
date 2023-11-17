import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Alias } from './partials/Alias'
import { Annet } from './partials/Annet'

const attrPaths = [
	'udistub.oppholdStatus',
	'udistub.arbeidsadgang',
	'udistub.aliaser',
	'udistub.flyktning',
	'udistub.soeknadOmBeskyttelseUnderBehandling',
]

export const udiAttributt = 'udistub'

export const UdistubForm = ({ formMethods }) => (
	<Vis attributt={attrPaths}>
		<Panel
			heading="UDI"
			hasErrors={panelError(formMethods.formState.errors, attrPaths)}
			iconType="udi"
			startOpen={erForsteEllerTest(formMethods.getValues(), [udiAttributt])}
		>
			<Kategori title="Gjeldende oppholdsstatus" vis="udistub.oppholdStatus">
				<Oppholdsstatus formMethods={formMethods} />
			</Kategori>
			<Arbeidsadgang formMethods={formMethods} />
			<Kategori title="Alias" vis="udistub.aliaser" flex={false}>
				<Alias />
			</Kategori>
			<Annet />
		</Panel>
	</Vis>
)

UdistubForm.validation = validation
