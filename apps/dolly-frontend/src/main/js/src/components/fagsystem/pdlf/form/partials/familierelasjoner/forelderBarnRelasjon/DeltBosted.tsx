import React, { useContext } from 'react'
import { DollyDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kategori } from '@/components/ui/form/kategori/Kategori'
import { UseFormReturn } from 'react-hook-form/dist/types'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'

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
	const opts: any = useContext(BestillingsveilederContext) as BestillingsveilederContextType
	const { harEksisterendeEktefellePartner, harEksisterendeFamilierelasjonBarn } =
		sjekkGyldigEksisterende(opts.personFoerLeggTil?.pdlforvalter)
	const harPartner =
		formMethods.watch('pdldata.person.sivilstand')?.length > 0 || harEksisterendeEktefellePartner
	const harForelderBarnRelasjon =
		formMethods.watch('pdldata.person.forelderBarnRelasjon')?.length > 0 ||
		harEksisterendeFamilierelasjonBarn

	return (
		<>
			{!harPartner && (
				<StyledAlert variant={'warning'} size={'small'}>
					For at delt bosted skal fungere, må bestilt ident ha en gjeldende sivilstand med en annen
					adresse enn hovedperson, dette kan velges i Steg 2 under familierelasjoner panel
				</StyledAlert>
			)}
			{!harForelderBarnRelasjon && (
				<StyledAlert variant={'warning'} size={'small'}>
					For at delt bosted skal fungere, må bestilt ident ha en relasjon til et barn, dette kan
					velges i Steg 2 under familierelasjoner panel
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
