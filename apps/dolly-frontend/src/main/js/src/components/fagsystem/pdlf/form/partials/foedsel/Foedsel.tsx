import React from 'react'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { initialFoedsel } from '~/components/fagsystem/pdlf/form/initialValues'
import { Yearpicker } from '~/components/ui/form/inputs/yearpicker/Yearpicker'
import _get from 'lodash/get'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'

const alderProps = ['alder', 'foedtEtter', 'foedtFoer']

export const Foedsel = ({ formikBag }) => {
	const handleLandChange = (selected, path) => {
		formikBag.setFieldValue(`${path}.foedeland`, selected?.value || null)
		if (selected?.value !== 'NOR') {
			formikBag.setFieldValue(`${path}.fodekommune`, null)
		}
	}

	const person = _get(formikBag.values, 'pdldata.opprettNyPerson')
	const hasAlder = () => {
		let funnetAlder = false
		alderProps.forEach((prop) => {
			if (person.hasOwnProperty(prop)) {
				funnetAlder = true
			}
		})
		return funnetAlder
	}

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.foedsel'}
				header="Fødsel"
				newEntry={initialFoedsel}
				canBeEmpty={false}
				hjelpetekst={
					hasAlder()
						? 'Du har valgt å sette både alder og fødsel på denne personen. Det er mulig å sette verdier som ikke samsvarer med hverandre. I så fall er det alder som vil bestemme identnummer, og fødsel som vil bestemme fødselsdato. '
						: null
				}
			>
				{(path: string, idx: number) => {
					const foedselsaar = _get(formikBag.values, `${path}.foedselsaar`)
					return (
						<>
							<FormikDatepicker
								name={`${path}.foedselsdato`}
								label="Fødselsdato"
								disabled={foedselsaar !== null}
								fastfield={false}
							/>
							<Yearpicker
								formikBag={formikBag}
								name={`${path}.foedselsaar`}
								label="Fødselsår"
								date={foedselsaar ? new Date(foedselsaar, 0) : null}
								handleDateChange={(val) =>
									formikBag.setFieldValue(`${path}.foedselsaar`, new Date(val).getFullYear())
								}
								maxDate={new Date()}
								disabled={_get(formikBag.values, `${path}.foedselsdato`) !== null}
							/>
							<FormikTextInput name={`${path}.foedested`} label="Fødested" size="large" />
							<FormikSelect
								name={`${path}.fodekommune`}
								label="Fødekommune"
								kodeverk={AdresseKodeverk.Kommunenummer}
								size="large"
								disabled={
									_get(formikBag.values, `${path}.foedeland`) !== 'NOR' &&
									_get(formikBag.values, `${path}.foedeland`) !== null
								}
							/>
							<FormikSelect
								name={`${path}.foedeland`}
								label="Fødeland"
								onChange={(selected) => handleLandChange(selected, path)}
								kodeverk={AdresseKodeverk.InnvandretUtvandretLand}
								size="large"
							/>
							<AvansertForm path={path} kanVelgeMaster={false} />
						</>
					)
				}}
			</FormikDollyFieldArray>
		</div>
	)
}
