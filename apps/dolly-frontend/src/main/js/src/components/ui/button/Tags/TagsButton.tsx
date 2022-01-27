import React, { useState } from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import useBoolean from '~/utils/hooks/useBoolean'
import DollyModal from '~/components/ui/modal/DollyModal'
import Button from '~/components/ui/button/Button'
import Loading from '~/components/ui/loading/Loading'

import './TagsButton.less'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

type Props = {
	action: Function
	loading: boolean
	gruppeId: number
	eksisterendeTags: [string]
}

export const TagsButton = ({ action, loading, eksisterendeTags }: Props) => {
	if (loading) return <Loading label="Sender tags..." />
	const [modalIsOpen, openModal, closeModal] = useBoolean(false)
	const [tags, setTags] = useState(eksisterendeTags)

	const tagsFraDolly = SelectOptionsOppslag.hentTagsFraDolly()
	const tagOptions = SelectOptionsOppslag.formatOptions('tags', tagsFraDolly)

	return (
		<React.Fragment>
			<Button onClick={openModal} kind="link">
				TILKNYTT TAGS
			</Button>
			<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="40%" overflow="auto">
				<div className="tagsModal">
					<div className="tagsModal tagsModal-content">
						<h1>TILKNYTT TAGS</h1>
						<h4>Velg hvilke tags du ønsker å legge til på denne gruppen</h4>
						<FormikSelect
							options={tagOptions}
							isLoading={tagsFraDolly.loading}
							size="large"
							isMulti={true}
							value={tags}
							// @ts-ignore
							onChange={(event: []) => setTags(event?.map((ev: { value: string }) => ev.value))}
						/>
					</div>
					<div className="tagsModal-actions">
						<NavButton type={'fare'} onClick={closeModal}>
							AVBRYT
						</NavButton>
						<NavButton
							onClick={() => {
								action(tags)
								location.reload()
							}}
							type="hoved"
						>
							TILKNYTT TAGS
						</NavButton>
					</div>
				</div>
			</DollyModal>
		</React.Fragment>
	)
}
