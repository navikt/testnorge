import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'
import { useFormContext } from 'react-hook-form'

export const aaregAttributt = 'aareg'

export const AaregForm = () => {
	const formMethods = useFormContext()
	return (
		<Vis attributt={aaregAttributt}>
			<Panel
				heading="Arbeidsforhold (Aareg)"
				hasErrors={panelError(formMethods.formState.errors, aaregAttributt)}
				iconType="arbeid"
				startOpen={erForsteEllerTest(formMethods.getValues(), [aaregAttributt])}
			>
				<ArbeidsforholdToggle />
			</Panel>
		</Vis>
	)
}

AaregForm.validation = validation
