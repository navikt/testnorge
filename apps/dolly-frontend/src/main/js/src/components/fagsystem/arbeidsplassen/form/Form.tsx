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
import { useFormContext } from 'react-hook-form'
import './CvForm.less'

export const arbeidsplassenAttributt = 'arbeidsplassenCV'
export const ArbeidsplassenForm = () => {
	const formMethods = useFormContext()
	return (
		<Vis attributt={arbeidsplassenAttributt}>
			<Panel
				heading="Nav CV"
				hasErrors={panelError(arbeidsplassenAttributt)}
				iconType="cv"
				startOpen={erForsteEllerTest(formMethods.getValues(), [arbeidsplassenAttributt])}
			>
				<div className="flexbox--flex-wrap cv-form">
					<JobboenskerForm />
					<UtdanningForm />
					<FagbrevForm />
					<ArbeidserfaringForm />
					<AnnenErfaringForm />
					<KompetanserForm />
					<OffentligeGodkjenningerForm />
					<AndreGodkjenningerForm />
					<SpraakForm />
					<FoererkortForm />
					<KursForm />
					<SammendragForm />
					<HjemmelForm />
				</div>
			</Panel>
		</Vis>
	)
}

ArbeidsplassenForm.validation = validation
