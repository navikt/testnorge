import { ArbeidserfaringForm } from '@/components/fagsystem/arbeidsplassen/form/partials/ArbeidserfaringForm'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { UtdanningForm } from '@/components/fagsystem/arbeidsplassen/form/partials/UtdanningForm'
import { JobboenskerForm } from '@/components/fagsystem/arbeidsplassen/form/partials/JobboenskerForm'

export const arbeidsplassenAttributt = 'arbeidsplassenCV'
export const ArbeidsplassenForm = ({ formikBag }) => {
	return (
		<Vis attributt={arbeidsplassenAttributt}>
			<Panel
				heading="Arbeidsplassen (CV)"
				hasErrors={panelError(formikBag, arbeidsplassenAttributt)}
				iconType="cv"
				startOpen={erForsteEllerTest(formikBag.values, [arbeidsplassenAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<JobboenskerForm formikBag={formikBag} />
					<UtdanningForm formikBag={formikBag} />
					<ArbeidserfaringForm formikBag={formikBag} />
				</div>
			</Panel>
		</Vis>
	)
}
