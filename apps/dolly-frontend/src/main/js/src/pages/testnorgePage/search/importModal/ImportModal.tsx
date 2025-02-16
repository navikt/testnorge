import React, { useState } from 'react'
import { useToggle } from 'react-use'
import { useNavigate } from 'react-router'
import useBoolean from '@/utils/hooks/useBoolean'
import { DollyModal } from '@/components/ui/modal/DollyModal'
import NavButton from '@/components/ui/button/NavButton/NavButton'
import { ImportPerson } from '@/pages/testnorgePage/search/SearchView'
import { DollyApi } from '@/service/Api'
import { PdlData } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { MalValg } from '@/pages/testnorgePage/search/importModal/MalValg'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import './ImportModal.less'
import { Gruppe } from '@/utils/hooks/useGruppe'
import { top } from '@popperjs/core'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

type Props = {
	valgtePersoner: ImportPerson[]
	importerPersoner: (
		valgtePersoner: ImportPerson[],
		mal: any,
		navigate: Function,
		gruppe?: Gruppe,
	) => void
	gruppe?: Gruppe
}

const getPdlPersoner = async (identer: string[]) => {
	return DollyApi.getPersonerFraPdl(identer)
		.then((response: any) => {
			const identerBolk = response?.data?.data?.hentIdenterBolk?.reduce(
				(map: any, person: any) => ({
					...map,
					[person.ident]: person.identer,
				}),
				{},
			)
			return response.data?.data?.hentPersonBolk?.map((ident: any) => {
				return {
					ident: ident.ident,
					data: {
						hentPerson: ident.person,
						hentIdenter: { identer: identerBolk?.[ident.ident] },
					},
				}
			})
		})
		.catch((_e: Error) => {
			return identer.map((ident) => {
				return {
					ident: ident,
					data: null,
				}
			})
		})
}

const partnerSivilstander = ['GIFT', 'REGISTRERT_PARTNER', 'SEPARERT', 'SEPARERT_PARTNER']

export const ImportModal = ({ valgtePersoner, importerPersoner, gruppe }: Props) => {
	const navigate = useNavigate()

	const [modalMalIsOpen, openMalModal, closeMalModal] = useBoolean(false)
	const [malData, setMalData] = useState(null)

	const [importMedPartner, toggleImportMedPartner] = useToggle(false)
	const [importMedMal, toggleImportMedMal] = useToggle(false)

	const personerValgt = valgtePersoner.length > 0

	const getPartnere = (data: PdlData[]) => {
		return data
			.filter((person) => !person.hentPerson?.doedsfall?.[0]?.doedsdato)
			.map((person) => person?.hentPerson?.sivilstand)
			.map((sivilstand) => {
				return sivilstand.filter(
					(siv) => !siv?.metadata?.historisk && partnerSivilstander.includes(siv?.type),
				)?.[0]?.relatertVedSivilstand
			})
			.filter(
				(partnerIdent) =>
					partnerIdent && !valgtePersoner.map((person) => person.ident).includes(partnerIdent),
			)
	}

	const handleImport = () => {
		importer(valgtePersoner, null)
	}

	const importer = (personer: ImportPerson[], mal: any) => {
		const partnere = getPartnere(valgtePersoner.map((person) => person.data))
		if (importMedPartner && partnere?.length > 0) {
			getPdlPersoner(partnere).then((response: ImportPerson[]) => {
				importerPersoner(valgtePersoner.concat(response), malData, navigate, gruppe)
			})
		} else {
			importerPersoner(personer, mal, navigate, gruppe)
		}
	}

	const utvalgtPersonersPartnere = getPartnere(valgtePersoner.map((person) => person.data))
	const visPartnereImport = !!(utvalgtPersonersPartnere && utvalgtPersonersPartnere.length)

	return (
		<React.Fragment>
			<div className="flexbox--baseline--justify-end import-knapper">
				{visPartnereImport && (
					<span className="flexbox--baseline--justify-end">
						<FormCheckbox
							id="import-modal-import-med-partner"
							checked={importMedPartner}
							onChange={toggleImportMedPartner}
							label="Import med partner"
						/>
						<Hjelpetekst placement={top}>
							En eller flere av dine valgte Test-Norge personer har en partner. <br /> Vil du
							inkludere partnerne i importen?
						</Hjelpetekst>
					</span>
				)}
				<div>
					<FormCheckbox
						id="import-modal-import-med-mal"
						checked={importMedMal}
						onChange={toggleImportMedMal}
						label="Benytt mal"
					/>
				</div>

				<NavButton
					type={'button'}
					variant={'primary'}
					onClick={() => {
						if (importMedMal) {
							openMalModal()
						} else {
							handleImport()
						}
					}}
					disabled={!personerValgt}
					title={!personerValgt ? 'Velg personer' : null}
				>
					Importer
				</NavButton>
			</div>

			<DollyModal isOpen={modalMalIsOpen} closeModal={closeMalModal} width="60%" overflow="auto">
				<div className="importModal">
					<div className="importModal importModal-content">
						<h1>Import med mal</h1>
					</div>
					<MalValg valgtMal={(mal: any) => setMalData(mal)} />
					<div className="importModal-actions">
						<NavButton
							type={'button'}
							onClick={() => importer(valgtePersoner, malData)}
							variant={'primary'}
						>
							Importer
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
