import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash-es'
import { ifPresent } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { MedServicebehov } from './partials/MedServicebehov'
import { AlertInntektskomponentenRequired } from '@/components/ui/brukerAlert/AlertInntektskomponentenRequired'
import { validation } from '@/components/fagsystem/arena/form/validation'
import { FormikCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { Alert } from '@navikt/ds-react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/Bestillingsveileder'

export const arenaPath = 'arenaforvalter'

export const ArenaForm = ({ formikBag }) => {
	const opts = useContext(BestillingsveilederContext)
	const { leggTilPaaGruppe } = opts?.is

	const servicebehovAktiv =
		_.get(formikBag.values, `${arenaPath}.arenaBrukertype`) === 'MED_SERVICEBEHOV'
	const dagpengerAktiv = _.get(formikBag.values, `${arenaPath}.dagpenger[0]`)

	useEffect(() => {
		servicebehovAktiv &&
			!_.get(formikBag.values, `${arenaPath}.kvalifiseringsgruppe`) &&
			formikBag.setFieldValue(`${arenaPath}.kvalifiseringsgruppe`, null)

		servicebehovAktiv &&
			formikBag.setFieldValue(`${arenaPath}.automatiskInnsendingAvMeldekort`, null)
	}, [])

	return (
		<Vis attributt={arenaPath}>
			<Panel
				heading="Arbeidsytelser"
				hasErrors={panelError(formikBag, arenaPath)}
				iconType="arena"
				startOpen={erForsteEllerTest(formikBag.values, [arenaPath])}
			>
				{!leggTilPaaGruppe && dagpengerAktiv && (
					<>
						{!formikBag.values.hasOwnProperty('inntektstub') && (
							<AlertInntektskomponentenRequired vedtak={'dagpengevedtak'} />
						)}
						<Alert variant={'info'} style={{ marginBottom: '20px' }}>
							For å kunne få gyldig dagpengevedtak må det være knyttet inntektsmelding for 12
							måneder før vedtakets fra dato. Dette kan enkelt gjøres i innteksinformasjon ved å
							benytte "Generer antall måneder" feltet.
						</Alert>
					</>
				)}
				{!servicebehovAktiv && (
					<FormikDatepicker
						name={`${arenaPath}.inaktiveringDato`}
						label="Inaktiv fra dato"
						disabled={servicebehovAktiv}
					/>
				)}
				{servicebehovAktiv && <MedServicebehov formikBag={formikBag} path={arenaPath} />}
				<FormikCheckbox
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
