import { useEffect } from 'react'

export const FasteDataWrapper = ({ fasteData, fetchFnrFraFasteData }) => {
    useEffect(() => {
        fetchFnrFraFasteData()
    })

    return fasteData
}
