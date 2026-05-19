import {
  collection,
  addDoc,
  getDocs,
  updateDoc,
  doc
} from 'firebase/firestore';

import { db } from '../firebase/config';

export const listarIdeias = async () => {
  const querySnapshot = await getDocs(collection(db, 'ideias'));

  return querySnapshot.docs.map((doc) => ({
    id: doc.id,
    ...doc.data()
  }));
};

export const aprovarIdeia = async (id) => {
  const ideiaRef = doc(db, 'ideias', id);

  await updateDoc(ideiaRef, {
    status: 'Aprovado'
  });
};

export const cadastrarProjeto = async (projeto) => {
  await addDoc(collection(db, 'projetos'), projeto);
};
