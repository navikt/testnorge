import React, { useContext, useEffect } from 'react'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { DollySelect, FormikSelect } from '@/components/ui/form/inputs/select/Select'
import {
	getInitialBostedsadresse,
	initialMatrikkeladresse,
	initialUkjentBosted,
	initialUtenlandskAdresse,
	initialVegadresse,
} from '@/components/fagsystem/pdlf/form/initialValues'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import * as _ from 'lodash'
import {
	MatrikkeladresseVelger,
	UkjentBosted,
	UtenlandskAdresse,
	VegadresseVelger,
} from '@/components/fagsystem/pdlf/form/partials/adresser/adressetyper'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { Adressetype } from '@/components/fagsystem/pdlf/PdlTypes'
import { getPlaceholder, setNavn } from '@/components/fagsystem/pdlf/form/partials/utils'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface BostedsadresseValues {
	formMethods: UseFormReturn
}

type BostedsadresseFormValues = {
	formMethods: UseFormReturn
	path: string
	idx?: number
	identtype?: string
}

type Target = {
	value: string
	label?: string
}

export const BostedsadresseForm = ({
	formMethods,
	path,
	idx,
	identtype,
}: BostedsadresseFormValues) => {
	useEffect(() => {
		formMethods.setValue(`${path}.adresseIdentifikatorFraMatrikkelen`, undefined)
		const boadresse = _.get(formMethods.getValues(), path)
		if (_.get(boadresse, 'vegadresse') && _.get(boadresse, 'vegadresse') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Veg)
		} else if (
			_.get(boadresse, 'matrikkeladresse') &&
			_.get(boadresse, 'matrikkeladresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Matrikkel)
		} else if (
			_.get(boadresse, 'utenlandskAdresse') &&
			_.get(boadresse, 'utenlandskAdresse') !== null
		) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Utenlandsk)
		} else if (_.get(boadresse, 'ukjentBosted') && _.get(boadresse, 'ukjentBosted') !== null) {
			formMethods.setValue(`${path}.adressetype`, Adressetype.Ukjent)
		}
	}, [])

	const valgtAdressetype = _.get(formMethods.getValues(), `${path}.adressetype`)

	const getAdresseOptions = () => {
		if (identtype && identtype !== 'FNR') {
			return Options('adressetypeUtenlandskBostedsadresse')
		}
		return Options('adressetypeBostedsadresse')
	}

	const handleChangeAdressetype = (target: Target, path: string) => {
		const adresse = _.get(formMethods.getValues(), path)
		const adresseClone = _.cloneDeep(adresse)

		_.set(adresseClone, 'adressetype', target?.value || null)

		if (!target) {
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
		}
		if (target?.value === 'VEGADRESSE') {
			_.set(adresseClone, 'vegadresse', initialVegadresse)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'MATRIKKELADRESSE') {
			_.set(adresseClone, 'matrikkeladresse', initialMatrikkeladresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'FREG')
		}
		if (target?.value === 'UTENLANDSK_ADRESSE') {
			_.set(adresseClone, 'utenlandskAdresse', initialUtenlandskAdresse)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'ukjentBosted', undefined)
			_.set(adresseClone, 'master', 'PDL')
		}
		if (target?.value === 'UKJENT_BOSTED') {
			_.set(adresseClone, 'ukjentBosted', initialUkjentBosted)
			_.set(adresseClone, 'vegadresse', undefined)
			_.set(adresseClone, 'matrikkeladresse', undefined)
			_.set(adresseClone, 'utenlandskAdresse', undefined)
			_.set(adresseClone, 'master', 'FREG')
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
					options={getAdresseOptions()}
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
			{valgtAdressetype === 'MATRIKKELADRESSE' && (
				<MatrikkeladresseVelger formMethods={formMethods} path={`${path}.matrikkeladresse`} />
			)}
			{valgtAdressetype === 'UTENLANDSK_ADRESSE' && (
				<UtenlandskAdresse
					formMethods={formMethods}
					path={`${path}.utenlandskAdresse`}
					master={_.get(formMethods.getValues(), `${path}.master`)}
				/>
			)}
			{valgtAdressetype === 'UKJENT_BOSTED' && <UkjentBosted path={`${path}.ukjentBosted`} />}
			<div className="flexbox--flex-wrap">
				<DatepickerWrapper>
					<FormikDatepicker name={`${path}.angittFlyttedato`} label="Flyttedato" />
					<FormikDatepicker name={`${path}.gyldigFraOgMed`} label="Gyldig f.o.m." addHour />
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
			<AvansertForm
				path={path}
				kanVelgeMaster={valgtAdressetype === null && identtype !== 'NPID'}
			/>
		</React.Fragment>
	)
}

export const Bostedsadresse = ({ formMethods }: BostedsadresseValues) => {
	const opts = useContext(BestillingsveilederContext)
	return (
		<Kategori title="Bostedsadresse">
			<FormikDollyFieldArray
				name="pdldata.person.bostedsadresse"
				header="Bostedsadresse"
				newEntry={getInitialBostedsadresse(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, idx: number) => (
					<BostedsadresseForm
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
