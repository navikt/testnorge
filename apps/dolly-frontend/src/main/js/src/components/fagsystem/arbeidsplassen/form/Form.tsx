import { ArbeidserfaringForm } from '@/components/fagsystem/arbeidsplassen/form/partials/ArbeidserfaringForm'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React from 'react'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { UtdanningForm } from '@/components/fagsystem/arbeidsplassen/form/partials/UtdanningForm'
import { JobboenskerForm } from '@/components/fagsystem/arbeidsplassen/form/partials/JobboenskerForm'
import { FagbrevForm } from '@/components/fagsystem/arbeidsplassen/form/partials/FagbrevForm'
import { AnnenErfaringForm } from '@/components/fagsystem/arbeidsplassen/form/partials/AnnenErfaringForm'
import { KompetanserForm } from '@/components/fagsystem/arbeidsplassen/form/partials/KompetanserForm'
import { OffentligeGodkjenningerForm } from '@/components/fagsystem/arbeidsplassen/form/partials/OffentligeGodkjenningerForm'
import { AndreGodkjenningerForm } from '@/components/fagsystem/arbeidsplassen/form/partials/AndreGodkjenningerForm'
import { SpraakForm } from '@/components/fagsystem/arbeidsplassen/form/partials/SpraakForm'
import { FoererkortForm } from '@/components/fagsystem/arbeidsplassen/form/partials/FoererkortForm'
import { KursForm } from '@/components/fagsystem/arbeidsplassen/form/partials/KursForm'
import { SammendragForm } from '@/components/fagsystem/arbeidsplassen/form/partials/SammendragForm'

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
					<FagbrevForm formikBag={formikBag} />
					<ArbeidserfaringForm formikBag={formikBag} />
					<AnnenErfaringForm formikBag={formikBag} />
					<KompetanserForm formikBag={formikBag} />
					<OffentligeGodkjenningerForm formikBag={formikBag} />
					<AndreGodkjenningerForm formikBag={formikBag} />
					<SpraakForm formikBag={formikBag} />
					<FoererkortForm formikBag={formikBag} />
					<KursForm formikBag={formikBag} />
					<SammendragForm formikBag={formikBag} />
				</div>
			</Panel>
		</Vis>
	)
}
