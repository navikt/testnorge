import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'
import { useFormContext } from 'react-hook-form'
import { initialArbeidsforholdOrg } from '@/components/fagsystem/aareg/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'

export const aaregAttributt = 'aareg'

export const AaregForm = () => {
	const formMethods = useFormContext()

	return (
		<Vis attributt={aaregAttributt}>
			<Panel
				heading="Arbeidsforhold (Aareg)"
				hasErrors={panelError(aaregAttributt)}
				iconType="arbeid"
				startOpen={erForsteEllerTest(formMethods.getValues(), [aaregAttributt])}
			>
				<FormDollyFieldArray
					name="aareg"
					header="Arbeidsforhold"
					newEntry={initialArbeidsforholdOrg}
					canBeEmpty={false}
				>
					{(path: string, idx: number) => <ArbeidsforholdToggle path={path} idx={idx} />}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

AaregForm.validation = validation
