import React, { useContext, useEffect } from 'react'
import * as _ from 'lodash-es'
import {
	getInitialKontaktadresse,
	initialPostboksadresse,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import {
	Postboksadresse,
	UtenlandskAdresse,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

interface KontaktadresseValues {
	formMethods: UseFormReturn
}

type KontaktadresseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	value: string
	label?: string
}

export const KontaktadresseForm = ({
	formMethods,
	path,
	idx,
	identtype,
}: KontaktadresseFormValues) => {
	useEffect(() => {
		formMethods.setValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const kontaktadresse = _.get(formMethods.getValues(), path)
		if (_.get(kontaktadresse, 'vegadresse') && _.get(kontaktadresse, 'vegadresse') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(kontaktadresse, 'utenlandskAdresse') &&
			_.get(kontaktadresse, 'utenlandskAdresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (
			_.get(kontaktadresse, 'postboksadresse') &&
			_.get(kontaktadresse, 'postboksadresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Postboks)
		}
	}, [])

	const valgtAdressetype = _.get(formMethods.getValues(), `${path}.adressetype`)

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _.get(formMethods.getValues(), path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'postboksadresse', undefined)
		}
		if (target?.value === 'POSTBOKSADRESSE') {
			_.set(adresseClone, 'postboksadresse', initialPostboksadresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
		}

		formMethods.setValue(path, adresseClone)
	}

	const { navnInfo, loading } = useGenererNavn()
	const navnOptions = SelectOptionsFormat.formatOptions('personnavn', navnInfo)

	return (
		<React.Fragment key={idx}>
			<div className="flexbox--full-width">
				<FormikSelect
					name={`${path}.adressetype`}
					label="Adressetype"
					options={Options('adressetypeKontaktadresse')}
					onChange={(target: Target) => handleChangeAdressetype(target, path)}
					size="large"
				/>
			</div>
			{valgtAdressetype === 'VEGADRESSE' && (
				<VegadresseVelger
					formMethods={formMethods}
					path={`${path}.vegadresse`}
					key={`veg_${idx}`}
				/>
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse
					formMethods={formMethods}
					path={`${path}.utenlandskAdresse`}
					master={_.get(formMethods.getValues(), `${path}.master`)}
				/>
			)}
			{valgtAdressetype === 'POSTBOKSADRESSE' && (
				<Postboksadresse path={`${path}.postboksadresse`} />
			)}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." />
					<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Gyldig t.o.m." />
				</DatepickerWrapper>
				<DollySelect
					name={`${path}.opprettCoAdresseNavn.fornavn`}
					label="C/O adressenavn"
					options={navnOptions}
					size="xlarge"
					placeholder={getPlaceholder(formMethods.getValues(), `${path}.opprettCoAdresseNavn`)}
					isLoading={loading}
					onChange={(navn: Target) =>
						setNavn(navn, `${path}.opprettCoAdresseNavn`, formMethods.setValue)
					}
					value={_.get(formMethods.getValues(), `${path}.opprettCoAdresseNavn.fornavn`)}
				/>
			</div>
			<AvansertForm path={path} kanVelgeMaster={identtype !== 'NPID'} />
		</React.Fragment>
	)
}

export const Kontaktadresse = ({ formMethods }: KontaktadresseValues) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<Kategori title="Kontaktadresse">
			<FormikDollyFieldArray
				name="pdldata.person.kontaktadresse"
				header="Kontaktadresse"
				newEntry={getInitialKontaktadresse(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<KontaktadresseForm
						formMethods={formMethods}
						path={path}
						idx={idx}
						identtype={opts?.identtype}
					/>
				)}
			</FormikDollyFieldArray>
		</Kategori>
	)
}
