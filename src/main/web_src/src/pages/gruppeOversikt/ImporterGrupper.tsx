import React, { useState } from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import { useAsync } from 'react-use'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import DollyModal from '~/components/ui/modal/DollyModal'
import { DollyApi } from '~/service/Api'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'

export default function ImporterGrupper() {
	const [ZIdent, setZIdent] = useState(null)
	const [isImportModalOpen, openImportModal, closeImportModal] = useBoolean(false)
	const ZIdenter = useAsync(async () => {
		const response = await DollyApi.getBrukere()
		return response.data
	}, [])

	const getZIdentOptions = () => {
		// console.log('ZIdenter.value :>> ', ZIdenter.value)
		return ZIdenter.value.reduce(function(filtered, ident) {
			if (ident.navIdent) {
				filtered.push({ value: ident.navIdent, label: ident.navIdent })
			}
			return filtered
		}, [])
	}

	const ZIdentOptions = ZIdenter.value ? getZIdentOptions() : []

	return (
		<>
			{/* // <AlertStripeInfo> */}
			<p>Du har for øyeblikket ingen testdatagrupper på denne brukerkontoen.</p>
			<p>
				Om dette er første gang du bruker din personlige brukerkonto kan du importere
				testdatagrupper Z-brukeren(e) du har benyttet tidligere ved å trykke på knappen nedenfor. Du
				kan når som helst importere testdatagrupper fra Z-brukere via Min side øverst til høyre.
			</p>
			<p>For å opprette en ny testdatagruppe, trykk på "Ny gruppe"-knappen over.</p>
			<NavButton type="hoved" onClick={openImportModal} style={{ marginTop: '10px' }}>
				Importer grupper
			</NavButton>
			{/* // </AlertStripeInfo> */}

			<DollyModal isOpen={isImportModalOpen} closeModal={closeImportModal}>
				<>
					<h1>Importer testdatagrupper fra Z-ident</h1>
					<DollySelect
						name="ZIdent"
						label="Z-ident"
						isLoading={ZIdenter.loading}
						options={ZIdentOptions}
						size="medium"
						onChange={e => setZIdent(e.value)}
						value={ZIdent}
						isClearable={false}
					/>
					<NavButton type="standard" onClick={closeImportModal} style={{ marginTop: '10px' }}>
						Avbryt
					</NavButton>
					<NavButton type="hoved" onClick={closeImportModal} style={{ marginTop: '10px' }}>
						Importer grupper
					</NavButton>
					{/* // TODO: Handle submit! */}
				</>
			</DollyModal>
		</>
	)
}
