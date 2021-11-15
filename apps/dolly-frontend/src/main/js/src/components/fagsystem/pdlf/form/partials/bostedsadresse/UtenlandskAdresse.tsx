import React from 'react'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'
import { initialUtenlandskAdresse } from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikProps } from 'formik'

interface UtenlandskAdresseForm {
	formikBag: FormikProps<{}>
}

export const UtenlandskAdresse = ({ formikBag }: UtenlandskAdresseForm) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.bostedsadresse"
			header="Utenlandsk boadresse"
			newEntry={{
				utenlandskAdresse: initialUtenlandskAdresse,
				kilde: 'Dolly',
				master: 'PDL',
				gjeldende: true,
			}}
			canBeEmpty={false}
		>
			{(path: string, idx: number) => {
				const utenlandskAdressePath = `${path}.utenlandskAdresse`
				const harAdressenavn =
					_get(formikBag.values, `${utenlandskAdressePath}.adressenavnNummer`) !== '' &&
					_get(formikBag.values, `${utenlandskAdressePath}.adressenavnNummer`) !== null
				const harPostboksnummer =
					_get(formikBag.values, `${utenlandskAdressePath}.postboksNummerNavn`) !== '' &&
					_get(formikBag.values, `${utenlandskAdressePath}.postboksNummerNavn`) !== null

				return (
					<>
						<div className="flexbox--flex-wrap" key={idx}>
							<FormikTextInput
								name={`${utenlandskAdressePath}.adressenavnNummer`}
								label="Gatenavn og husnummer"
								// @ts-ignore
								disabled={harPostboksnummer}
							/>
							<FormikTextInput
								name={`${utenlandskAdressePath}.postboksNummerNavn`}
								label="Postboksnummer og -navn"
								// @ts-ignore
								disabled={harAdressenavn}
							/>
							<FormikTextInput name={`${utenlandskAdressePath}.postkode`} label="Postkode" />
							<FormikTextInput name={`${utenlandskAdressePath}.bySted`} label="By eller sted" />
							<FormikSelect
								name={`${utenlandskAdressePath}.landkode`}
								label="Land"
								kodeverk={AdresseKodeverk.StatsborgerskapLand}
								isClearable={false}
								size="large"
							/>
							<FormikTextInput
								name={`${utenlandskAdressePath}.bygningEtasjeLeilighet`}
								label="Bygg-/leilighetsinfo"
							/>
							<FormikTextInput
								name={`${utenlandskAdressePath}.regionDistriktOmraade`}
								label="Region/distrikt/område"
							/>
						</div>
						<AvansertForm path={path} kanVelgeMaster={false} />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
