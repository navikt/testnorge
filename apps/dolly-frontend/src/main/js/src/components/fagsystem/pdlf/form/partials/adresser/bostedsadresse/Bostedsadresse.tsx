import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { AdresseKodeverk } from '~/config/kodeverk'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import {
	initialBostedsadresse,
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import _get from 'lodash/get'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UtenlandskAdresse'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import { UkjentBosted } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UkjentBosted'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Matrikkeladresse'
import { Vegadresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Vegadresse'
import { VegadresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/VegadresseVelger'
import styled from 'styled-components'

export const Bostedsadresse = ({ formikBag }) => {
	const handleChangeAdressetype = (target, path) => {
		const adresse = _get(formikBag.values, path)
		const adresseClone = _cloneDeep(adresse)

		_set(adresseClone, 'adressetype', target?.value || null)
		if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'UKJENT_BOSTED') {
			_set(adresseClone, 'ukjentBosted', initialUkjentBosted)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
		}

		formikBag.setFieldValue(path, adresseClone)
	}

	return (
		<Kategori title="Bostedsadresse">
			<FormikDollyFieldArray
				name="pdldata.person.bostedsadresse"
				header="Bostedsadresse"
				newEntry={initialBostedsadresse}
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
									options={Options('adressetypePdl')}
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
							{valgtAdressetype === 'MATRIKKELADRESSE' && (
								<Matrikkeladresse formikBag={formikBag} path={`${path}.matrikkeladresse`} />
							)}
							{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
								<UtenlandskAdresse formikBag={formikBag} path={`${path}.utenlandskAdresse`} />
							)}
							{valgtAdressetype === 'UKJENT_BOSTED' && (
								<UkjentBosted formikBag={formikBag} path={`${path}.ukjentBosted`} />
							)}
							<div className="flexbox--full-width">
								<FormikDatepicker name={`${path}.angittFlyttedato`} label="Flyttedato" />
							</div>
							<AvansertForm path={path} kanVelgeMaster={false} />
						</React.Fragment>
					)
				}}
			</FormikDollyFieldArray>
		</Kategori>
	)
}
