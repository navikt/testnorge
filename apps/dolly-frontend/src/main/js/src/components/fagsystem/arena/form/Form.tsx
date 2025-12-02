import React from 'react'
import * as _ from 'lodash-es'
import { ifPresent } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { MedServicebehov } from './partials/MedServicebehov'
import { AlertInntektskomponentenRequired } from '@/components/ui/brukerAlert/AlertInntektskomponentenRequired'
import { validation } from '@/components/fagsystem/arena/form/validation'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { useFormContext } from 'react-hook-form'

export const arenaPath = 'arenaforvalter'

export const ArenaForm = () => {
	const formMethods = useFormContext()
	const opts = useBestillingsveileder() as BestillingsveilederContextType
	const leggTilPaaGruppe = opts?.is?.leggTilPaaGruppe

	const servicebehovAktiv = formMethods.watch(`${arenaPath}.arenaBrukertype`) === 'MED_SERVICEBEHOV'

	const dagpengerAktiv = formMethods.watch(`${arenaPath}.dagpenger[0]`)

	const personFoerLeggTilInntektstub = _.get(opts.personFoerLeggTil, 'inntektstub')

	const registrertDato = opts?.personFoerLeggTil?.arenaforvalteren
		?.map((miljo) => miljo?.data?.registrertDato)
		?.find((dato) => dato)

	return (
		<Vis attributt={arenaPath}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(arenaPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formMethods.getValues(), [arenaPath])}
			>
				{!leggTilPaaGruppe &&
					dagpengerAktiv &&
					!personFoerLeggTilInntektstub &&
					!formMethods.getValues().hasOwnProperty('inntektstub') && (
						<AlertInntektskomponentenRequired vedtak={'dagpengevedtak'} />
					)}
				{!servicebehovAktiv && (
					<div className={'flexbox--flex-wrap'}>
						<FormDatepicker
							name={`${arenaPath}.inaktiveringDato`}
							label="Inaktiv fra dato"
							disabled={servicebehovAktiv}
							minDate={registrertDato ? new Date(registrertDato) : null}
						/>
						{!opts.personFoerLeggTil?.arenaforvalteren && (
							<FormDatepicker
								name={`${arenaPath}.aktiveringDato`}
								label="Aktiveringsdato"
								minDate={new Date('2002-12-30')}
							/>
						)}
					</div>
				)}
				{servicebehovAktiv && <MedServicebehov formMethods={formMethods} path={arenaPath} />}
				<FormCheckbox
					name={`${arenaPath}.automatiskInnsendingAvMeldekort`}
					label="Automatisk innsending av meldekort"
					size="small"
				/>
			</Panel>
		</Vis>
	)
}

ArenaForm.validation = {
	arenaforvalter: ifPresent('$arenaforvalter', validation),
}
