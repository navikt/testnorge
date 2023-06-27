import { DollyTextInput, FormikTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { GtKodeverk } from '@/config/kodeverk'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import * as _ from 'lodash-es'
import { FormikProps } from 'formik'
import { useEffect } from 'react'

interface UtenlandskAdresseForm {
	formikBag: FormikProps<{}>
	path: string
	master?: string | unknown
}

export const UtenlandskAdresse = ({ formikBag, path, master }: UtenlandskAdresseForm) => {
	// const [masterTest, setMasterTest] = useState(master)

	const harAdressenavn =
		_.get(formikBag.values, `${path}.adressenavnNummer`) !== '' &&
		_.get(formikBag.values, `${path}.adressenavnNummer`) !== null

	const harPostboksnummer =
		_.get(formikBag.values, `${path}.postboksNummerNavn`) !== '' &&
		_.get(formikBag.values, `${path}.postboksNummerNavn`) !== null

	// const bygningEtasjeLeilighet = _.get(formikBag.values, `${path}.bygningEtasjeLeilighet`)
	// const regionDistriktOmraade = _.get(formikBag.values, `${path}.regionDistriktOmraade`)

	useEffect(() => {
		// setMasterTest(master)

		if (master !== 'PDL') {
			formikBag.setFieldValue(`${path}.bygningEtasjeLeilighet`, null)
			formikBag.setFieldValue(`${path}.regionDistriktOmraade`, null)
		}
	}, [master])

	return (
		<div className="flexbox--flex-wrap">
			<FormikTextInput
				name={`${path}.adressenavnNummer`}
				label="Gatenavn og husnummer"
				// @ts-ignore
				isDisabled={harPostboksnummer}
			/>
			<FormikTextInput
				name={`${path}.postboksNummerNavn`}
				label="Postboksnummer og -navn"
				// @ts-ignore
				isDisabled={harAdressenavn}
			/>
			<FormikTextInput name={`${path}.postkode`} label="Postkode" />
			<FormikTextInput name={`${path}.bySted`} label="By eller sted" />
			<FormikSelect
				name={`${path}.landkode`}
				label="Land"
				kodeverk={GtKodeverk.LAND}
				isClearable={false}
				size="large"
			/>
			{master === 'PDL' ? (
				<>
					<FormikTextInput
						name={`${path}.bygningEtasjeLeilighet`}
						label="Bygg-/leilighetsinfo"
						// onBlur={(e: any) => {
						// 	formikBag.setFieldValue(`${path}.bygningEtasjeLeilighet`, e.target.value)
						// 	setBygningEtasjeLeilighet(e.target.value)
						// }}
						// placeholder="Tetstitest"
						// onInput={(e: any) =>
						// 	formikBag.setFieldValue(`${path}.bygningEtasjeLeilighet`, e.target.value)
						// }
						// isDisabled={masterTest !== 'PDL'}
						// title={masterTest !== 'PDL' ? 'Kan bare settes hvis master er PDL' : null}
						// defaultValue={masterTest !== 'PDL' ? null : bygningEtasjeLeilighet}
						// defaultValue={''}
						// value={bygningEtasjeLeilighet}
						// value={watch(`${path}.bygningEtasjeLeilighet`)}
						// fastfield="false"
						// useOnChange={true}
						// enablereinitialize
						// visHvisAvhuket={false}
					/>
					<FormikTextInput
						name={`${path}.regionDistriktOmraade`}
						label="Region/distrikt/område"
						// isDisabled={master !== 'PDL'}
						// title={master !== 'PDL' ? 'Kan bare settes hvis master er PDL' : null}
					/>
				</>
			) : (
				<>
					<DollyTextInput
						name={undefined}
						// name={`${path}.bygningEtasjeLeilighet`}
						// value={null}
						label="Bygg-/leilighetsinfo"
						title={'Kan bare settes hvis master er PDL'}
						isDisabled={true}
						// readOnly
					/>
					<DollyTextInput
						name={undefined}
						// value={null}
						label="Region/distrikt/område"
						title={'Kan bare settes hvis master er PDL'}
						isDisabled={true}
						// readOnly
					/>
				</>
			)}
		</div>
	)
}
