import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import { requiredDate, ifPresent, requiredString } from '~/utils/YupValidations'
import { Vis, pathAttrs } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { MedServicebehov } from './partials/MedServicebehov'

const _initialValues = {
	arenaforvalter: {
		arenaBrukertype: Options('arenaBrukertype')[0].value,
		inaktiveringDato: null,
		kvalifiseringsgruppe: null
	}
}

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv =
		_get(formikBag, 'values.arenaforvalter.arenaBrukertype') === 'MED_SERVICEBEHOV'

	// Bytter struktur på hele skjema
	const handleAfterChange = val => {
		if (val === 'MED_SERVICEBEHOV') {
			formikBag.setFieldValue('arenaforvalter.inaktiveringDato', null)
		} else {
			formikBag.setFieldValue('arenaforvalter', _initialValues.arenaforvalter)
		}
	}

	return (
		<Vis attributt={pathAttrs.kategori.arena}>
			<Panel heading="Arena" hasErrors={panelError(formikBag)}>
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

ArenaForm.initialValues = attrs => {
	return attrs.arenaforvalter ? _initialValues : {}
}

const validation = Yup.object({
	aap: Yup.array().of(
		Yup.object({
			fraDato: Yup.date(),
			tilDato: requiredDate
		})
	),
	aap115: Yup.array().of(
		Yup.object({
			fraDato: requiredDate
		})
	),
	arenaBrukertype: requiredString,
	inaktiveringDato: requiredDate,
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
