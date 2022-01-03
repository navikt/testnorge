import React from 'react'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import {
	initialKontaktadresse,
	initialPostboksadresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { VegadresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/VegadresseVelger'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UtenlandskAdresse'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { Postboksadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Postboksadresse'

export const Kontaktadresse = ({ formikBag }) => {
	const handleChangeAdressetype = (target, path) => {
		const adresse = _get(formikBag.values, path)
		const adresseClone = _cloneDeep(adresse)

		_set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'POSTBOKSADRESSE') {
			_set(adresseClone, 'postboksadresse', initialPostboksadresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
		}

		formikBag.setFieldValue(path, adresseClone)
	}

	return (
		<Kategori title="Kontaktadresse">
			<FormikDollyFieldArray
				name="pdldata.person.kontaktadresse"
				header="Kontaktadresse"
				newEntry={initialKontaktadresse}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => {
					const valgtAdressetype = _get(formikBag.values, `${path}.adressetype`)

					return (
						<React.Fragment key={idx}>
							<div className="flexbox--full-width">
								<FormikSelect
									name={`${path}.adressetype`}
									label="Adressetype"
									options={Options('adressetypeKontaktadresse')}
									onChange={(target) => handleChangeAdressetype(target, path)}
									size="large"
								/>
							</div>
							{valgtAdressetype === 'VEGADRESSE' && (
								<VegadresseVelger
									formikBag={formikBag}
									path={`${path}.vegadresse`}
									id={idx}
									key={`veg_${idx}`}
								/>
							)}
							{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
								<UtenlandskAdresse formikBag={formikBag} path={`${path}.utenlandskAdresse`} />
							)}
							{valgtAdressetype === 'POSTBOKSADRESSE' && (
								<Postboksadresse formikBag={formikBag} path={`${path}.postboksadresse`} />
							)}
							<AvansertForm path={path} kanVelgeMaster={false} />
						</React.Fragment>
					)
				}}
			</FormikDollyFieldArray>
		</Kategori>
	)
}
