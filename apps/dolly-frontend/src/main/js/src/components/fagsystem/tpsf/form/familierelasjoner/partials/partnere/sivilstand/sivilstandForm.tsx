import React from 'react'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { addDays } from 'date-fns'
import { DollyDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { statuser as SivilstandStatuser } from './SivilstandOptions'
import { Sivilstand } from '../partnere/partnerTypes'

interface SivilstandForm {
	formikPath: string
	sivilstand: Sivilstand
	options: Array<Options>
	readOnly: boolean
	formikBag: FormikProps<{}>
	minimumDato: string
	tidligereSivilstand: boolean
}

type Options = {
	value: string
	label: string
}

export default ({
	formikPath,
	sivilstand,
	options,
	readOnly,
	formikBag,
	tidligereSivilstand,
	minimumDato = null,
}: SivilstandForm) => (
	<div
		className="flexbox"
		title={
			tidligereSivilstand
				? 'Du kan ikke endre sivilstand fra tidligere bestilling'
				: readOnly
				? 'Du kan kun endre siste sivilstand på siste partner'
				: undefined
		}
	>
		<DollySelect
			name={`${formikPath}.sivilstand`}
			value={sivilstand.sivilstand}
			onChange={(e: Options) => formikBag.setFieldValue(`${formikPath}.sivilstand`, e.value)}
			label="Forhold til partner (sivilstand)"
			options={readOnly ? Object.values(SivilstandStatuser) : options}
			isClearable={false}
			disabled={readOnly}
			fastfield={false}
			feil={getFeilmelding(formikBag, `${formikPath}.sivilstand`)}
		/>
		<DollyDatepicker
			value={sivilstand.sivilstandRegdato}
			onChange={(e: string) => formikBag.setFieldValue(`${formikPath}.sivilstandRegdato`, e)}
			label="Sivilstand fra dato"
			isClearable={false}
			disabled={readOnly}
			fastfield={false}
			minDate={addDays(new Date(minimumDato), 3)}
			feil={getFeilmelding(formikBag, `${formikPath}.sivilstandRegdato`)}
		/>
	</div>
)

const getFeilmelding = (formikBag: FormikProps<{}>, path: string) => {
	const feilmelding = _get(formikBag.errors, path)
	return feilmelding ? { feilmelding: feilmelding } : null
}
