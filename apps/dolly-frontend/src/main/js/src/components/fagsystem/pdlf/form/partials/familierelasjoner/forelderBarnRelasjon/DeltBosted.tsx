import React from 'react'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { UseFormReturn } from 'react-hook-form/dist/types'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import {
	BestillingsveilederContextType,
	useBestillingsveileder,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { gyldigeSivilstander } from '@/components/fagsystem/pdlf/form/partials/familierelasjoner/sivilstand/Sivilstand'

interface DeltBostedValues {
	formMethods: UseFormReturn
	path: string
}

const sjekkGyldigEksisterende = (pdlForvalterValues: any) => {
	const harEktefellePartner = pdlForvalterValues?.relasjoner?.some(
		(relasjon: any) => relasjon.relasjonType === 'EKTEFELLE_PARTNER',
	)
	const harFamilierelasjonBarn = pdlForvalterValues?.relasjoner?.some(
		(relasjon: any) => relasjon.relasjonType === 'FAMILIERELASJON_BARN',
	)
	return {
		harEksisterendeEktefellePartner: harEktefellePartner,
		harEksisterendeFamilierelasjonBarn: harFamilierelasjonBarn,
	}
}

export const DeltBostedForm = ({ formMethods, path }: DeltBostedValues) => {
	const opts = useBestillingsveileder() as BestillingsveilederContextType

	const { harEksisterendeEktefellePartner, harEksisterendeFamilierelasjonBarn } =
		sjekkGyldigEksisterende(opts.personFoerLeggTil?.pdlforvalter)

	const gyldigePartnere = formMethods
		.watch('pdldata.person.sivilstand')
		?.filter(
			(sivilstand: any) =>
				gyldigeSivilstander.includes(sivilstand.type) && sivilstand.borIkkeSammen,
		)

	const harPartner = gyldigePartnere?.length > 0 || harEksisterendeEktefellePartner

	const gyldigeBarn = formMethods
		.watch('pdldata.person.forelderBarnRelasjon')
		?.filter((forelderBarn: any) => forelderBarn.relatertPersonsRolle === 'BARN')

	const harForelderBarnRelasjon = gyldigeBarn?.length > 0 || harEksisterendeFamilierelasjonBarn

	return (
		<>
			{!harPartner && (
				<StyledAlert variant={'warning'} size={'small'}>
					For at delt bosted skal fungere, må personen ha en gjeldende partner med en annen adresse.
					Velg sivilstand (partner) i steg 2 under familierelasjoner-panelet, og huk av for "bor
					ikke sammen" i steg 3.
				</StyledAlert>
			)}
			{!harForelderBarnRelasjon && (
				<StyledAlert variant={'warning'} size={'small'}>
					For at delt bosted skal fungere, må personen ha en relasjon til et barn. Dette kan velges
					i steg 2 under familierelasjoner-panelet.
				</StyledAlert>
			)}
			<div className="flexbox--flex-wrap">
				<DollyDatepicker name={`${path}.startdatoForKontrakt`} label="Startdato for kontrakt" />
				<DollyDatepicker name={`${path}.sluttdatoForKontrakt`} label="Sluttdato for kontrakt" />
			</div>
		</>
	)
}

export const DeltBosted = ({ formMethods, path }: DeltBostedValues) => {
	return (
		<Kategori title="Delt bosted">
			<DeltBostedForm formMethods={formMethods} path={path} />
		</Kategori>
	)
}
