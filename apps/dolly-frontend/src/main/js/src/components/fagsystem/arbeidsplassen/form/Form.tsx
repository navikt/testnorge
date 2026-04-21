import { ArbeidserfaringForm } from '@/components/fagsystem/arbeidsplassen/form/partials/ArbeidserfaringForm'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import React, { useContext } from 'react'
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
import { useFormContext } from 'react-hook-form'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import * as _ from 'lodash-es'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

export const arbeidsplassenAttributt = 'arbeidsplassenCV'
export const ArbeidsplassenForm = () => {
	const formMethods = useFormContext()

	const opts = useContext(BestillingsveilederContext)
	const personFoerLeggTil = opts?.personFoerLeggTil

	const harArbeidssoekerregisteret =
		_.has(formMethods.getValues(), 'arbeidssoekerregisteret') ||
		_.has(personFoerLeggTil, 'arbeidssoekerregisteret')

	return (
		<Vis attributt={arbeidsplassenAttributt}>
			<Panel
				heading="Nav CV"
				hasErrors={panelError(arbeidsplassenAttributt)}
				iconType="cv"
				startOpen={erForsteEllerTest(formMethods.getValues(), [arbeidsplassenAttributt])}
			>
				{!harArbeidssoekerregisteret && (
					<StyledAlert
						variant={'warning'}
						size={'small'}
						style={{ marginBottom: 0, wordBreak: 'auto-phrase' }}
					>
						For å kunne registrere CV må personen være under oppfølging av Nav. Derfor må "Er
						arbeidssøker" under Arbeidssøkerregisteret velges på forrige steg.
					</StyledAlert>
				)}
				<div className="flexbox--flex-wrap">
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
				</div>
			</Panel>
		</Vis>
	)
}

ArbeidsplassenForm.validation = validation
