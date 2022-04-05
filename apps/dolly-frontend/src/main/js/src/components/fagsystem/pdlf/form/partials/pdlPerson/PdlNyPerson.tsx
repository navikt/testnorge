import React, { useContext } from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'
import _get from 'lodash/get'
import { BestillingsveilederContext } from '~/components/bestillingsveileder/Bestillingsveileder'
import { FormikProps } from 'formik'

interface PdlNyPersonValues {
	nyPersonPath: string
	eksisterendePersonPath?: string
	formikBag?: FormikProps<{}>
	erNyIdent?: boolean
}

export const PdlNyPerson = ({
	nyPersonPath,
	eksisterendePersonPath,
	formikBag,
	erNyIdent = false,
}: PdlNyPersonValues) => {
	const opts = useContext(BestillingsveilederContext)
	const isLeggTil = opts?.is?.leggTil
	const disableAlder =
		_get(formikBag.values, `${nyPersonPath}.foedtEtter`) != null ||
		_get(formikBag.values, `${nyPersonPath}.foedtFoer`) != null

	const disableFoedtDato = _get(formikBag.values, `${nyPersonPath}.alder`) != ''

	const identtypeOptions =
		erNyIdent && isLeggTil
			? Options('identtype').filter((a) => a.value !== 'NPID')
			: Options('identtype')

	const hasEksisterendePerson =
		eksisterendePersonPath && _get(formikBag?.values, eksisterendePersonPath) !== null

	return (
		<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
			<FormikSelect
				name={`${nyPersonPath}.identtype`}
				label="Identtype"
				options={identtypeOptions}
				disabled={hasEksisterendePerson}
			/>
			<FormikSelect
				name={`${nyPersonPath}.kjoenn`}
				label="Kjønn"
				options={Options('kjoenn')}
				disabled={hasEksisterendePerson}
			/>
			<FormikTextInput
				name={`${nyPersonPath}.alder`}
				type="number"
				label="Alder"
				disabled={disableAlder || hasEksisterendePerson}
			/>
			<FormikDatepicker
				name={`${nyPersonPath}.foedtEtter`}
				label="Født etter"
				disabled={disableFoedtDato || hasEksisterendePerson}
				fastfield={false}
			/>
			<FormikDatepicker
				name={`${nyPersonPath}.foedtFoer`}
				label="Født før"
				disabled={disableFoedtDato || hasEksisterendePerson}
				fastfield={false}
			/>
			{!erNyIdent && (
				<FormikSelect
					name={`${nyPersonPath}.statsborgerskapLandkode`}
					label="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					size="large"
					disabled={hasEksisterendePerson}
				/>
			)}
			{!erNyIdent && (
				<FormikSelect
					name={`${nyPersonPath}.gradering`}
					label="Gradering"
					options={Options('gradering')}
					disabled={hasEksisterendePerson}
				/>
			)}
			<FormikCheckbox
				name={`${nyPersonPath}.syntetisk`}
				label="Er syntetisk"
				disabled={hasEksisterendePerson}
				checkboxMargin={erNyIdent}
			/>
			<FormikCheckbox
				name={`${nyPersonPath}.nyttNavn.hasMellomnavn`}
				label="Har mellomnavn"
				disabled={hasEksisterendePerson}
				checkboxMargin={erNyIdent}
			/>
		</div>
	)
}
