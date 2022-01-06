import React from 'react'
import _get from 'lodash/get'
import _cloneDeep from 'lodash/cloneDeep'
import _set from 'lodash/set'
import {
	initialMatrikkeladresse,
	initialOppholdAnnetSted,
	initialOppholdsadresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '~/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { VegadresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/VegadresseVelger'
import { Matrikkeladresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/Matrikkeladresse'
import { UtenlandskAdresse } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/UtenlandskAdresse'
import { AvansertForm } from '~/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { OppholdAnnetSted } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/OppholdAnnetSted'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { MatrikkeladresseVelger } from '~/components/fagsystem/pdlf/form/partials/adresser/adressetyper/MatrikkeladresseVelger'

export const Oppholdsadresse = ({ formikBag }) => {
	const handleChangeAdressetype = (target, path) => {
		const adresse = _get(formikBag.values, path)
		const adresseClone = _cloneDeep(adresse)

		_set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'oppholdAnnetSted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_set(adresseClone, 'vegadresse', initialVegadresse)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'oppholdAnnetSted', undefined)
			_set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'oppholdAnnetSted', undefined)
			_set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'oppholdAnnetSted', undefined)
			_set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'OPPHOLD_ANNET_STED') {
			_set(adresseClone, 'oppholdAnnetSted', initialOppholdAnnetSted)
			_set(adresseClone, 'vegadresse', undefined)
			_set(adresseClone, 'matrikkeladresse', undefined)
			_set(adresseClone, 'utenlandskAdresse', undefined)
			_set(adresseClone, 'master', 'FREG')
		}

		formikBag.setFieldValue(path, adresseClone)
	}

	return (
		<Kategori title="Oppholdsadresse">
			<FormikDollyFieldArray
				name="pdldata.person.oppholdsadresse"
				header="Oppholdsadresse"
				newEntry={initialOppholdsadresse}
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
									options={Options('adressetypeOppholdsadresse')}
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
								<MatrikkeladresseVelger formikBag={formikBag} path={`${path}.matrikkeladresse`} />
							)}
							{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
								<UtenlandskAdresse formikBag={formikBag} path={`${path}.utenlandskAdresse`} />
							)}
							{valgtAdressetype === 'OPPHOLD_ANNET_STED' && (
								<OppholdAnnetSted formikBag={formikBag} path={`${path}.oppholdAnnetSted`} />
							)}
							<div className="flexbox--flex-wrap">
								<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
								<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
							</div>
							<AvansertForm
								path={path}
								kanVelgeMaster={
									valgtAdressetype !== 'MATRIKKELADRESSE' &&
									valgtAdressetype !== 'OPPHOLD_ANNET_STED'
								}
							/>
						</React.Fragment>
					)
				}}
			</FormikDollyFieldArray>
		</Kategori>
	)
}
