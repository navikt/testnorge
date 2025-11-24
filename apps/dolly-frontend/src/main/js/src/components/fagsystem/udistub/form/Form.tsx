import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { Oppholdsstatus } from './partials/Oppholdsstatus'
import { Arbeidsadgang } from './partials/Arbeidsadgang'
import { Annet } from './partials/Annet'
import { useFormContext } from 'react-hook-form'

const attrPaths = [
	'udistub.oppholdStatus',
	'udistub.arbeidsadgang',
	'udistub.flyktning',
	'udistub.soeknadOmBeskyttelseUnderBehandling',
]

export const udiAttributt = 'udistub'

export const UdistubForm = () => {
	const formMethods = useFormContext()
	return (
		<Vis attributt={attrPaths}>
			<Panel
				heading="UDI"
				hasErrors={panelError(attrPaths)}
				iconType="udi"
				startOpen={erForsteEllerTest(formMethods.getValues(), [udiAttributt])}
			>
				<Kategori title="Gjeldende oppholdsstatus" vis="udistub.oppholdStatus">
					<Oppholdsstatus formMethods={formMethods} />
				</Kategori>
				<Arbeidsadgang formMethods={formMethods} />
				<Annet />
			</Panel>
		</Vis>
	)
}

UdistubForm.validation = validation
