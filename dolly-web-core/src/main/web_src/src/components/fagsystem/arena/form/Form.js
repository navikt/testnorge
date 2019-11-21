import React from 'react'
import * as Yup from 'yup'
import _get from 'lodash/get'
import Panel from '~/components/ui/panel/Panel'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { panelError } from '~/components/ui/form/formUtils'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { MedServicebehov } from './partials/MedServicebehov'

export const ArenaForm = ({ formikBag }) => {
	const servicebehovAktiv = formikBag.values.arenaforvalter.arenaBrukertype === 'MED_SERVICEBEHOV'

	// Bytter struktur på hele skjema
	const handleAfterChange = val => {
		if (val === 'MED_SERVICEBEHOV') {
			formikBag.setFieldValue('arenaforvalter.inaktiveringDato', null)
		} else {
			formikBag.setFieldValue('arenaforvalter', ArenaForm.initialValues().arenaforvalter)
		}
	}

	return (
		<Panel heading="Arena" hasErrors={panelError(formikBag)} startOpen>
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
	)
}

ArenaForm.initialValues = () => {
	return {
		arenaforvalter: {
			arenaBrukertype: Options('arenaBrukertype')[0].value,
			inaktiveringDato: null,
			kvalifiseringsgruppe: null
		}
	}
}

ArenaForm.validation = {
	arenaforvalter: Yup.object({
		aap: Yup.array()
			.of(
				Yup.object({
					fraDato: Yup.date(),
					tilDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
				})
			)
			.nullable(),
		aap115: Yup.array()
			.of(
				Yup.object({
					fraDato: Yup.string().typeError('Formatet må være DD.MM.YYYY.')
				})
			)
			.nullable(),
		arenaBrukertype: Yup.string().required('Vennligst velg'),
		inaktiveringDato: Yup.date().nullable(),
		kvalifiseringsgruppe: Yup.string()
			.nullable()
			.when('arenaBrukertype', {
				is: 'MED_SERVICEBEHOV',
				then: Yup.string().required('Påkrevd felt')
			})
	})
}
