import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from '@/components/shared/ArbeidsforholdToggle/ArbeidsforholdToggle'
import { useFormContext } from 'react-hook-form'
import {
	initialArbeidsforholdOrg,
	initialArbeidsgiverOrg,
	initialArbeidsgiverPers,
} from '@/components/fagsystem/aareg/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ArbeidsforholdForm } from '@/components/fagsystem/aareg/form/partials/arbeidsforholdForm'
import React, { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'

export const aaregAttributt = 'aareg'

export const AaregForm = () => {
	const formMethods = useFormContext()

	const { personFoerLeggTil } = useContext(
		BestillingsveilederContext,
	) as BestillingsveilederContextType
	const tidligereAaregdata = hentAaregEksisterendeData(personFoerLeggTil)

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
					lockedEntriesLength={tidligereAaregdata?.length || 0}
				>
					{(path: string, idx: number) => (
						<>
							<ArbeidsforholdToggle
								formMethods={formMethods}
								path={path}
								idx={idx}
								useFormState={true}
								useValidation={true}
								initialArbeidsgiverOrg={initialArbeidsgiverOrg}
								initialArbeidsgiverPers={initialArbeidsgiverPers}
							/>
							<ArbeidsforholdForm
								path={path}
								arbeidsforholdIndex={idx}
								tidligereAaregdata={tidligereAaregdata}
							/>
						</>
					)}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}

AaregForm.validation = validation
