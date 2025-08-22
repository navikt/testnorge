import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { validation } from './validation'
import { ArbeidsforholdToggle } from './partials/arbeidsforholdToggle'
import { useFormContext } from 'react-hook-form'
import { initialArbeidsforholdOrg } from '@/components/fagsystem/aareg/form/initialValues'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ArbeidsforholdForm } from '@/components/fagsystem/aareg/form/partials/arbeidsforholdForm'
import React, { useContext } from 'react'
import {
	useDollyFasteDataOrganisasjoner,
	useDollyOrganisasjoner,
} from '@/utils/hooks/useDollyOrganisasjoner'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import { getEgneOrganisasjoner } from '@/utils/EgneOrganisasjoner'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { hentAaregEksisterendeData } from '@/components/fagsystem/aareg/form/utils'

export const aaregAttributt = 'aareg'

export const AaregForm = () => {
	const formMethods = useFormContext()
	const { currentBruker } = useCurrentBruker()

	const { organisasjoner: fasteOrganisasjoner, loading: fasteOrganisasjonerLoading } =
		useDollyFasteDataOrganisasjoner(true)

	const { organisasjoner: brukerOrganisasjoner, loading: brukerOrganisasjonerLoading } =
		useDollyOrganisasjoner(currentBruker?.brukerId)
	const egneOrganisasjoner = getEgneOrganisasjoner(brukerOrganisasjoner)

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
								path={path}
								idx={idx}
								fasteOrganisasjoner={fasteOrganisasjoner}
								brukerOrganisasjoner={brukerOrganisasjoner}
								egneOrganisasjoner={egneOrganisasjoner}
								loadingOrganisasjoner={fasteOrganisasjonerLoading || brukerOrganisasjonerLoading}
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
