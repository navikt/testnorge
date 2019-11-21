import React from 'react'
import _get from 'lodash/get'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { DollyCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

export const MedServicebehov = ({ formikBag }) => {
	const { arenaforvalter } = formikBag.values

	const handleChange115 = event => {
		formikBag.setFieldValue(
			'arenaforvalter.aap115',
			event.target.checked ? [{ fraDato: null }] : null
		)
	}
	const handleChangeAAP = event => {
		formikBag.setFieldValue(
			'arenaforvalter.aap',
			event.target.checked ? [{ fraDato: null, tilDato: null }] : null
		)
	}

	return (
		<React.Fragment>
			<FormikSelect
				name="arenaforvalter.kvalifiseringsgruppe"
				label="Servicebehov"
				options={Options('kvalifiseringsgruppe')}
				size="large"
			/>
			<Kategori title="11-5-vedtak">
				<DollyCheckbox
					id="har115vedtak"
					label="Har 11-5-vedtak"
					checked={Boolean(arenaforvalter.aap115)}
					onChange={handleChange115}
				/>

				<FormikDatepicker name="arenaforvalter.aap115[0].fraDato" label="Fra dato" />
			</Kategori>

			<Kategori title="AAP-vedtak UA - positivt utfall">
				<DollyCheckbox
					id="harAAPvedtak"
					label="Har AAP-vedtak"
					checked={Boolean(arenaforvalter.aap)}
					onChange={handleChangeAAP}
				/>
				<FormikDatepicker name="arenaforvalter.aap[0].fraDato" label="Fra dato" />
				<FormikDatepicker name="arenaforvalter.aap[0].tilDato" label="Til dato" />
			</Kategori>
		</React.Fragment>
	)
}
