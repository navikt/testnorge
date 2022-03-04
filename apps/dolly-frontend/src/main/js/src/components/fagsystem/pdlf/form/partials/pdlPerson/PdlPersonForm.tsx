import React, { useContext } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { FormikProps } from 'formik'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'

interface PdlPersonValues {
	path: string
	formikBag: FormikProps<{}>
	erNyIdent: boolean
}

export const PdlPersonForm = ({ path, formikBag, erNyIdent = false }: PdlPersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const isLeggTil = opts?.is?.leggTil

	const disableAlder =
		_get(formikBag.values, `${path}.foedtEtter`) != null ||
		_get(formikBag.values, `${path}.foedtFoer`) != null

	const disableFoedtDato = _get(formikBag.values, `${path}.alder`) != ''

	const identtypeOptions =
		erNyIdent && isLeggTil
			? Options('identtype').filter((a) => a.value !== 'NPID')
			: Options('identtype')

	return (
		<>
			<FormikSelect name={`${path}.identtype`} label="Identtype" options={identtypeOptions} />
			<FormikSelect name={`${path}.kjoenn`} label="Kjønn" options={Options('kjoenn')} />
			<FormikTextInput name={`${path}.alder`} type="number" label="Alder" disabled={disableAlder} />
			<FormikDatepicker
				name={`${path}.foedtEtter`}
				label="Født etter"
				disabled={disableFoedtDato}
				fastfield={false}
			/>
			<FormikDatepicker
				name={`${path}.foedtFoer`}
				label="Født før"
				disabled={disableFoedtDato}
				fastfield={false}
			/>
			{!erNyIdent && (
				<FormikSelect
					name={`${path}.statsborgerskapLandkode`}
					label="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					size="large"
				/>
			)}
			{!erNyIdent && (
				<FormikSelect name={`${path}.gradering`} label="Gradering" options={Options('gradering')} />
			)}
			<FormikCheckbox name={`${path}.syntetisk`} label="Er syntetisk" checkboxMargin />
			<FormikCheckbox
				name={`${path}.nyttNavn.hasMellomnavn`}
				label="Har mellomnavn"
				checkboxMargin
			/>
		</>
	)
}
