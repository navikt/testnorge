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
import { validation } from '@/components/fagsystem/arbeidsplassen/form/validation'
import { HjemmelForm } from '@/components/fagsystem/arbeidsplassen/form/partials/HjemmelForm'

export const arbeidsplassenAttributt = 'arbeidsplassenCV'
export const ArbeidsplassenForm = ({ formMethods }) => {
	return (
		<Vis attributt={arbeidsplassenAttributt}>
			<Panel
				heading="Arbeidsplassen (CV)"
				hasErrors={panelError(formMethods.formState.errors, arbeidsplassenAttributt)}
				iconType="cv"
				startOpen={erForsteEllerTest(formMethods.getValues(), [arbeidsplassenAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<JobboenskerForm formMethods={formMethods} />
					<UtdanningForm formMethods={formMethods} />
					<FagbrevForm formMethods={formMethods} />
					<ArbeidserfaringForm formMethods={formMethods} />
					<AnnenErfaringForm formMethods={formMethods} />
					<KompetanserForm formMethods={formMethods} />
					<OffentligeGodkjenningerForm formMethods={formMethods} />
					<AndreGodkjenningerForm formMethods={formMethods} />
					<SpraakForm formMethods={formMethods} />
					<FoererkortForm formMethods={formMethods} />
					<KursForm formMethods={formMethods} />
					<SammendragForm formMethods={formMethods} />
					<HjemmelForm />
				</div>
			</Panel>
		</Vis>
	)
}

ArbeidsplassenForm.validation = validation
