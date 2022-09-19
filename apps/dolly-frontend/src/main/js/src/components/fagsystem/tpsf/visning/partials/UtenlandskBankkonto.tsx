import React from 'react'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Button from "~/components/ui/button/Button";
import useBoolean from "~/utils/hooks/useBoolean";
import styled from "styled-components";

const EditDeleteKnapper = styled.div`
	position: absolute;
	right: 0;
	top: 0;
	margin: -5px 10px 0 0;
	&&& {
		button {
			position: relative;
		}
	}
`

type Data = {
	data: UtenlandskBankkontoData
	extraButtons: boolean
}

type UtenlandskBankkontoData = {
	kontonummer: string
	tilfeldigKontonummer: boolean
	swift: string
	landkode: string
	banknavn?: string
	bankkode?: string
	iban: string
	valuta: string
	bankAdresse1?: string
	bankAdresse2?: string
	bankAdresse3?: string
}

export const Visning = ({data, extraButtons}: Data) => {
	console.log('Visning, data', data)
	console.log('visning extra', extraButtons)
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	return (
	<div style={{position: 'relative'}}>
		<div className="person-visning_content">
			<ErrorBoundary>
				<TitleValue title={'Kontonummer'} value={data.kontonummer} />
				<TitleValue title={'TilfeldigKontonummer'} value={data.tilfeldigKontonummer ? 'Ja' : ''} />
				<TitleValue title={'Swift kode'} value={data.swift} />
				<TitleValue title={'Land'} kodeverk={GtKodeverk.LAND} value={data.landkode} />
				<TitleValue title={'Banknavn'} value={data.banknavn} />
				<TitleValue title={'Bankkode'} value={data.bankkode} />
				<TitleValue title={'Valuta'} kodeverk={ArbeidKodeverk.Valutaer} value={data.valuta} />
				<TitleValue title={'Adresselinje 1'} value={data.bankAdresse1} />
				<TitleValue title={'Adresselinje 2'} value={data.bankAdresse2} />
				<TitleValue title={'Adresselinje 3'} value={data.bankAdresse3} />
			</ErrorBoundary>
		</div>
		{ extraButtons &&
			<EditDeleteKnapper>
				<Button kind="trashcan" onClick={() => console.log('slett konto')} title="Slett" />
			</EditDeleteKnapper>
		}
	</div>
)}

export const UtenlandskBankkonto = ({ data, extraButtons=null }: Data) => {
	if (!data) {
		return null
	}
	return (
		<div>
			<SubOverskrift label="Utenlandsk bankkonto" iconKind="bankkonto" />
			<Visning data={data} extraButtons={extraButtons} />
		</div>
	)
}
