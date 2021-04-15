import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { isAfter } from 'date-fns'
import { requiredDate, ifPresent, requiredString, messages } from '~/utils/YupValidations'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { MedServicebehov } from './partials/MedServicebehov'

const arenaAttributt = 'arenaforvalter'

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv =
		_get(formikBag, 'values.arenaforvalter.arenaBrukertype') === 'MED_SERVICEBEHOV'

	// Bytter struktur på hele skjema
	const handleAfterChange = val => {
		if (val.value === 'MED_SERVICEBEHOV') {
			formikBag.setFieldValue('arenaforvalter.inaktiveringDato', null)
		} else {
			formikBag.setFieldValue('arenaforvalter', {
				arenaBrukertype: 'UTEN_SERVICEBEHOV',
				inaktiveringDato: null,
				kvalifiseringsgruppe: null
			})
		}
	}

	return (
		<Vis attributt={arenaAttributt}>
			<Panel
				heading="Arena"
				hasErrors={panelError(formikBag, arenaAttributt)}
				iconType="arena"
				startOpen={() => erForste(formikBag.values, [arenaAttributt])}
			>
				<FormikSelect
					name="arenaforvalter.arenaBrukertype"
					label="Brukertype"
					afterChange={handleAfterChange}
					options={Options('arenaBrukertype')}
					isClearable={false}
				/>
				{!servicebehovAktiv && (
					<FormikDatepicker
						name="arenaforvalter.inaktiveringDato"
						label="Inaktiv fra dato"
						disabled={servicebehovAktiv}
					/>
				)}
				{servicebehovAktiv && <MedServicebehov formikBag={formikBag} />}
			</Panel>
		</Vis>
	)
}

const validation = Yup.object({
	aap: Yup.array().of(
		Yup.object({
			fraDato: requiredDate,
			tilDato: Yup.string()
				.test('etter-fradato', 'Til-dato må være etter fra-dato', function validDate(tildato) {
					const values = this.options.context
					const fradato = values.arenaforvalter.aap[0].fraDato
					if (!fradato || !tildato) return true
					return isAfter(new Date(tildato), new Date(fradato))
				})
				.required(messages.required)
				.nullable()
		})
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: requiredDate
		})
	),
	arenaBrukertype: requiredString,
	inaktiveringDato: Yup.mixed()
		.nullable()
		.when('arenaBrukertype', {
			is: 'UTEN_SERVICEBEHOV',
			then: requiredDate
		}),
	kvalifiseringsgruppe: Yup.string()
		.nullable()
		.when('arenaBrukertype', {
			is: 'MED_SERVICEBEHOV',
			then: requiredString
		})
})

ArenaForm.validation = {
	arenaforvalter: ifPresent('$arenaforvalter', validation)
}
