import React from 'react'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type Data = {
	data: UtenlandskBankkontoData
}

type UtenlandskBankkontoData = {
	kontonummer: string
	tilfeldigKontonummer: boolean
	swift: string
	landkode: string
	banknavn?: string
	iban: string
	valuta: string
	bankAdresse1?: string
	bankAdresse2?: string
	bankAdresse3?: string
}

export const Visning = ({ data }: Data) => (
	<>
		<div className="person-visning_content">
			<ErrorBoundary>
				<TitleValue title={'Kontonummer'} value={data.kontonummer} />
				<TitleValue title={'TilfeldigKontonummer'} value={data.tilfeldigKontonummer ? 'Ja' : ''} />
				<TitleValue title={'Swift kode'} value={data.swift} />
				<TitleValue title={'Land'} kodeverk={GtKodeverk.LAND} value={data.landkode} />
				<TitleValue title={'Banknavn'} value={data.banknavn} />
				<TitleValue title={'Bankkode'} value={data.iban} />
				<TitleValue title={'Valuta'} kodeverk={ArbeidKodeverk.Valutaer} value={data.valuta} />
				<TitleValue title={'Adresselinje 1'} value={data.bankAdresse1} />
				<TitleValue title={'Adresselinje 2'} value={data.bankAdresse2} />
				<TitleValue title={'Adresselinje 3'} value={data.bankAdresse3} />
			</ErrorBoundary>
		</div>
	</>
)

export const UtenlandskBankkonto = ({ data }: Data) => {
	if (!data) return null
	return (
		<div>
			<SubOverskrift label="Utenlandsk bankkonto" iconKind="bankkonto" />
			<Visning data={data} />
		</div>
	)
}
