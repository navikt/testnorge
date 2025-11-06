import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingData, BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { ArbeidKodeverk, GtKodeverk } from '@/config/kodeverk'

type UtenlandskBankkontoTypes = {
	utenlandskBankkonto: {
		kontonummer: string
		tilfeldigKontonummer: boolean
		swift: string
		landkode: string
		banknavn: string
		iban: string
		valuta: string
		bankAdresse1: string
		bankAdresse2: string
		bankAdresse3: string
	}
}
export const UtenlandskBankkonto = ({ utenlandskBankkonto }: UtenlandskBankkontoTypes) => {
	if (!utenlandskBankkonto) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Utenlandsk bankkonto</BestillingTitle>
				<BestillingData>
					<TitleValue title="Kontonummer" value={utenlandskBankkonto.kontonummer} />
					<TitleValue
						title="Tilfeldig kontonummer"
						value={utenlandskBankkonto.tilfeldigKontonummer && 'Ja'}
					/>
					<TitleValue title="Swift-kode" value={utenlandskBankkonto.swift} />
					<TitleValue
						title="Land"
						value={utenlandskBankkonto.landkode}
						kodeverk={GtKodeverk.LAND}
					/>
					<TitleValue title="Banknavn" value={utenlandskBankkonto.banknavn} />
					<TitleValue title="Bankkode" value={utenlandskBankkonto.iban} />
					<TitleValue
						title="Valuta"
						value={utenlandskBankkonto.valuta}
						kodeverk={ArbeidKodeverk.Valutaer}
					/>
					<TitleValue title="Adresselinje 1" value={utenlandskBankkonto.bankAdresse1} />
					<TitleValue title="Adresselinje 2" value={utenlandskBankkonto.bankAdresse2} />
					<TitleValue title="Adresselinje 3" value={utenlandskBankkonto.bankAdresse3} />
				</BestillingData>
			</ErrorBoundary>
		</div>
	)
}
