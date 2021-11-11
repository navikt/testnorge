import React from 'react'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import _get from 'lodash/get'

export const UtenlandskAdresse = ({ formikBag }) => {
	return (
		<FormikDollyFieldArray
			name="pdldata.person.bostedsadresse"
			header="Utenlandsk boadresse"
			newEntry={null} // TODO fix
			canBeEmpty={false}
		>
			{(path, idx) => {
				const utenlandskAdressePath = `${path}.utenlandskAdresse`
				const master = _get(formikBag.values, `${path}.master`)
				return (
					<>
						<div className="flexbox--flex-wrap" key={idx}>
							<FormikTextInput
								name={`${utenlandskAdressePath}.adressenavnNummer`}
								label="Gatenavn og husnummer"
							/>
							<FormikTextInput
								name={`${utenlandskAdressePath}.postboksNummerNavn`}
								label="Postboksnummer og -navn"
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
							{master === 'PDL' ? (
								<>
									{/* if master er PDL:*/}
									<FormikTextInput
										name={`${utenlandskAdressePath}.bygningEtasjeLeilighet`}
										label="Bygg-/leilighetsinfo"
									/>
									<FormikTextInput
										name={`${utenlandskAdressePath}.regionDistriktOmraade`}
										label="Region/distrikt/område"
									/>
								</>
							) : (
								<>
									{/* if master er FREG:*/}
									<FormikTextInput name={`${utenlandskAdressePath}.bygning`} label="Navn på bygg" />
									<FormikTextInput
										name={`${utenlandskAdressePath}.etasjenummer`}
										label="Etasje i bygg"
									/>
									<FormikTextInput
										name={`${utenlandskAdressePath}.boenhet`}
										label="Leilighetsnummer o.l."
									/>
									<FormikTextInput name={`${utenlandskAdressePath}.region`} label="Region" />
									<FormikTextInput
										name={`${utenlandskAdressePath}.distriktsnavn`}
										label="Distrikt"
									/>
								</>
							)}
						</div>
						<AvansertForm path={path} />
					</>
				)
			}}
		</FormikDollyFieldArray>
	)
}
